package org.codehaus.plexus.embed;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.PropertyUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * <tt>Embedder</tt> enables a client to embed Plexus into their
 * application with a minimal amount of work.  The basic usage is
 * as follows:
 * <br/>
 * <pre>
 *     Embedder embedder = new Embedder();
 *     embedder.setConfiguration("/plexus.xml");
 *     embedder.addContextValue("plexus.home", ".");
 *     embedder.start();
 *
 *     PlexusContainer container = embedder.getContainer();
 *     [do stuff with container]
 *
 *     embedder.stop();
 * </pre>
 * <br/>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="pete-codehaus-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id$
 */
public class Embedder
{
    private String configuration;

    private volatile URL configurationURL;
    
    /** Context properties */
    private Properties properties;

    private final DefaultPlexusContainer container;

    private volatile boolean embedderStarted = false;

    private volatile boolean embedderStopped = false;

    public Embedder()
    {
        container = new DefaultPlexusContainer();
    }

    public PlexusContainer getContainer()
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

    public boolean hasService( String role )
    {
        return getContainer().hasComponent( role );
    }

    public boolean hasService( String role, String id )
    {
        return getContainer().hasComponent( role, id );
    }

    public void release( Object service )
    {
        getContainer().release( service );
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        container.setClassLoader( classLoader );
    }

    public void setConfiguration( String configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configuration = configuration;
    }

    public void setConfiguration( URL configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        this.configurationURL = configuration;
    }

    public void addContextValue( Object key, Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException( "Embedder has already been started" );
        }

        container.addContextValue( key, value );
    }

    
    public void setProperties( Properties properties )
    {
         this.properties = properties;
    }
    
    public void setProperties( File file ) 
    {
        properties = PropertyUtils.loadProperties( file );        
    }
    
    protected void initializeContext()
    {
        Set keys = properties.keySet();
        for ( Iterator iter = keys.iterator(); iter.hasNext(); )
        {
            String key = ( String ) iter.next();
            String value = properties.getProperty( key );           
            container.addContextValue( key, value );
        }        
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
