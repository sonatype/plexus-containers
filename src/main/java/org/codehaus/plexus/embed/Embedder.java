package org.codehaus.plexus.embed;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Embedder
    implements PlexusEmbedder
{
    private Reader configurationReader;

    /**
     * Context properties
     */
    private Properties properties;

    private DefaultPlexusContainer container;

    private boolean embedderStarted = false;

    private boolean embedderStopped = false;

    private Map context = new HashMap();

    public Embedder( Map context,
                     String configuration )
        throws EmbedderException
    {
        this( context, configuration, null );
    }

    public Embedder( Map context,
                     String configuration,
                     ClassWorld classWorld )
        throws EmbedderException
    {
        try
        {
            container = new DefaultPlexusContainer( "plexus", context, configuration, classWorld );

            embedderStarted = true;
        }
        catch ( PlexusContainerException e )
        {
            throw new EmbedderException( "Error creating embedder. " + e.getMessage(), e );
        }
    }

    public Embedder()
        throws EmbedderException
    {
    }

    public synchronized PlexusContainer getContainer()
    {
        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder must be started" );
        }

        return container;
    }

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return getContainer().lookup( role );
    }

    public Object lookup( String role,
                          String id )
        throws ComponentLookupException
    {
        return getContainer().lookup( role, id );
    }

    public boolean hasComponent( String role )
    {
        return getContainer().hasComponent( role );
    }

    public boolean hasComponent( String role,
                                 String id )
    {
        return getContainer().hasComponent( role, id );
    }

    public void release( Object service )
        throws ComponentLifecycleException
    {
        getContainer().release( service );
    }

    //public synchronized void setClassLoader( ClassLoader classLoader )
    //{
    //    container.setClassLoader( classLoader );
    //}

    public synchronized void setClassWorld( ClassWorld classWorld )
    {
        container.setClassWorld( classWorld );
    }

    public synchronized void setConfiguration( URL configuration )
        throws IOException
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configurationReader = new InputStreamReader( configuration.openStream() );
    }

    public synchronized void setConfiguration( Reader configuration )
        throws IOException
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configurationReader = configuration;
    }

    public synchronized void addContextValue( Object key,
                                              Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        context.put( key, value );
    }

    public synchronized void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    public synchronized void setProperties( File file )
    {
        properties = PropertyUtils.loadProperties( file );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setLoggerManager( LoggerManager loggerManager )
    {
        container.setLoggerManager( loggerManager );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected synchronized void initializeContext()
    {
        Set keys = properties.keySet();

        for ( Iterator iter = keys.iterator(); iter.hasNext(); )
        {
            String key = (String) iter.next();

            String value = properties.getProperty( key );

            context.put( key, value );
        }
    }

    public synchronized void start( ClassWorld classWorld )
        throws PlexusContainerException
    {
        container.setClassWorld( classWorld );

        start();
    }

    public synchronized void start()
        throws PlexusContainerException
    {
        if ( embedderStarted )
        {
            throw new IllegalStateException( "Embedder already started" );
        }

        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder cannot be restarted" );
        }

        if ( properties != null )
        {
            initializeContext();
        }

        container = new DefaultPlexusContainer( null, context, null, null );

        embedderStarted = true;
    }

    public synchronized void stop()
    {
        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder already stopped" );
        }

        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder not started" );
        }

        container.dispose();

        embedderStarted = false;

        embedderStopped = true;
    }
}
