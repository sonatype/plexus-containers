package org.codehaus.plexus.embed;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultArtifactEnabledContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class ArtifactEnabledEmbedder
{
    private URL configurationURL;

    /** Context properties */
    private Properties properties;

    private final DefaultArtifactEnabledContainer container;

    private boolean embedderStarted = false;

    private boolean embedderStopped = false;

    public ArtifactEnabledEmbedder()
    {
        container = new DefaultArtifactEnabledContainer();
    }

    public synchronized PlexusContainer getContainer()
    {
        if ( !embedderStarted )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder must be started" );
        }

        return container;
    }

    public Object lookup( String role ) throws ComponentLookupException
    {
        return getContainer().lookup( role );
    }

    public Object lookup( String role, String id ) throws ComponentLookupException
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
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder has already been started" );
        }

        this.configurationURL = configuration;
    }

    public synchronized void addContextValue( Object key, Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder has already been started" );
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
            String key = (String) iter.next();

            String value = properties.getProperty( key );

            container.addContextValue( key, value );
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
            throw new IllegalStateException( "ArtifactEnabledEmbedder already started" );
        }

        if ( embedderStopped )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder cannot be restarted" );
        }

        if ( configurationURL != null )
        {
            try
            {
                container.setConfigurationResource( new InputStreamReader( configurationURL.openStream() ) );
            }
            catch ( PlexusConfigurationResourceException e )
            {
                throw new PlexusContainerException( "Error loading from configuration reader", e );
            }
            catch ( IOException e )
            {
                throw new PlexusContainerException( "Error loading from configuration reader", e );
            }
        }

        if ( properties != null )
        {
            initializeContext();
        }

        container.initialize();

        embedderStarted = true;

        container.start();
    }

    public synchronized void stop()
    {
        if ( embedderStopped )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder already stopped" );
        }

        if ( !embedderStarted )
        {
            throw new IllegalStateException( "ArtifactEnabledEmbedder not started" );
        }

        container.dispose();

        embedderStarted = false;

        embedderStopped = true;
    }
}
