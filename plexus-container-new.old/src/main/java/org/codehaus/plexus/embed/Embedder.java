package org.codehaus.plexus.embed;

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

public class Embedder
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
