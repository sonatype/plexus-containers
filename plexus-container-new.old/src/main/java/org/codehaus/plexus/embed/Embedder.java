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

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.PropertyUtils;

public class Embedder implements PlexusEmbedder
{

    private URL configurationURL;
    
    /** Context properties */
    private Properties properties;

    private final DefaultPlexusContainer container;

    private boolean embedderStarted = false;

    private boolean embedderStopped = false;

    public Embedder()
    {
        container = new DefaultPlexusContainer();
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

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return getContainer().lookup( role, id );
    }

    public boolean hasComponent( String role )
    {
        return getContainer().hasComponent( role );
    }

    public boolean hasComponent( String role, String id )
    {
        return getContainer().hasComponent( role, id );
    }

    public void release( Object service )
        throws Exception
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
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configurationURL = configuration;
    }

    public synchronized void addContextValue( Object key, Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        container.addContextValue( key, value );
    }

    
    public synchronized void setProperties( Properties properties )
    {
         this.properties = properties;
    }
    
    public synchronized void setProperties( File file ) 
    {
        properties = PropertyUtils.loadProperties( file );        
    }
    
    protected synchronized void initializeContext()
    {
        Set keys = properties.keySet();

        for ( Iterator iter = keys.iterator(); iter.hasNext(); )
        {
            String key = ( String ) iter.next();

            String value = properties.getProperty( key );

            container.addContextValue( key, value );
        }        
    }

    public synchronized void start( ClassWorld classWorld )
        throws Exception
    {
        container.setClassWorld( classWorld );

        start();
    }

    public synchronized void start()
        throws Exception
    {
        if ( embedderStarted )
        {
            throw new IllegalStateException( "Embedder already started" );
        }

        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder cannot be restarted" );
        }

        if ( configurationURL != null )
        {
            container.setConfigurationResource( new InputStreamReader( configurationURL.openStream() ) );
        }
        
        if ( properties != null)
        {
            initializeContext();
        }    

        container.initialize();

        embedderStarted = true;

        container.start();
    }

    public synchronized void stop()
        throws Exception
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
