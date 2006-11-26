package org.codehaus.plexus.embed;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.ClassWorldAdapter;
import org.codehaus.classworlds.ClassWorldReverseAdapter;
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

    private Properties properties;

    private DefaultPlexusContainer container;

    private boolean embedderStarted = false;

    private boolean embedderStopped = false;

    private Map context = new HashMap();

    public Embedder()
    {
    }

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
            if ( classWorld == null )
            {
                container = new DefaultPlexusContainer( "plexus", context, configuration, null );
            }
            else
            {
                container = new DefaultPlexusContainer( "plexus", context, configuration,
                                                        ClassWorldReverseAdapter.getInstance( classWorld ) );
            }

            embedderStarted = true;
        }
        catch ( PlexusContainerException e )
        {
            throw new EmbedderException( "Error creating embedder. " + e.getMessage(), e );
        }
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
        container.setClassWorld( ClassWorldReverseAdapter.getInstance( classWorld ) );
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
        container.setClassWorld( ClassWorldReverseAdapter.getInstance( classWorld ) );

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
