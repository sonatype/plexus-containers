package org.codehaus.plexus.embed;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
    /** Configuration resource or file. */
    private String configuration;

    /** Configuration resource URL */
    private URL configurationURL;

    /** Plexus Container. */
    private PlexusContainer container;

    /** Flag to indicate embedder has been started. */
    private boolean embedderStarted = false;

    /** Flag to indicate embedder has been stopped. */
    private boolean embedderStopped = false;

    /**
     * Default constructor.
     */
    public Embedder()
    {
        container = new DefaultPlexusContainer();
    }

    /**
     * Gets the <tt>PlexusContainer</tt> that was started by the
     * embedder.
     *
     * @return The <tt>PlexusContainer</tt> that was started.
     * @throws IllegalStateException If the embedder has not already
     * been started.
     */
    public PlexusContainer getContainer()
    {
        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder must be started" );
        }

        return container;
    }

    public Object lookup( String role ) throws ServiceException
    {
        return getContainer().getComponentRepository().lookup( role );
    }

    public Object lookup( String role, String id ) throws ServiceException
    {
        return getContainer().getComponentRepository().lookup( role, id );
    }

    public boolean hasService( String role )
    {
        return getContainer().getComponentRepository().hasService( role );
    }

    public boolean hasService( String role, String id )
    {
        return getContainer().getComponentRepository().hasService( role, id );
    }

    public void release( Object service )
    {
        getContainer().getComponentRepository().release( service );
    }

    /**
     * Set the configuration for the <tt>PlexusContainer</tt>.  This
     * configuration can either be a file or a resource in the
     * classpath.
     *
     * @deprecated avoid this function - use @see setConfiguration(URL) instead
     *
     * @param configuration A file or resource in the classpath that
     * contains the configuration for the <tt>PlexusContainer</tt>.
     * @throws IllegalStateException If the embedder has already been
     * started or stopped.
     */
    public void setConfiguration( String configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException(
                "Embedder has already been started" );
        }

        this.configuration = configuration;
    }

    /**
     * Set the configuration for the <tt>PlexusContainer</tt>.
     *
     * @param configurationURL A URL that contains the configuration
     * for the <tt>PlexusContainer</tt>.
     * @throws IllegalStateException If the embedder has already been
     * started or stopped.
     */
    public void setConfiguration( URL configuration )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException(
                "Embedder has already been started" );
        }

        this.configurationURL = configuration;
    }


    /**
     * Add a value to the <tt>PlexusContainer</tt>'s context.
     *
     * @param key The key for the context value.
     * @param value The value to be inserted.
     * @throws IllegalStateException If the embedder has already been
     * started or stopped.
     */
    public void addContextValue( Object key, Object value )
    {
        if ( embedderStarted || embedderStopped )
        {
            throw new IllegalStateException(
                "Embedder has already been started" );
        }

        container.addContextValue( key, value );
    }

    /**
     * Start the <tt>PlexusContainer<tt>.  This container can then be
     * fetched via <tt>getContainer</tt> for use by clients.
     *
     * @throws Exception If there was an error starting the container.
     * @throws IllegalStateException If the embedder has already been
     * started, or if its already been stopped.  The embedder cannot
     * be restarted.
     */
    public void start()
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

        container.setConfigurationResource( new InputStreamReader( findConfigurationInputStream() ) );

        container.initialize();

        embedderStarted = true;

        container.start();
    }

    /**
     * Stop the <tt>PlexusContainer</tt>.  Once the container has been
     * stopped, it cannot be restarted.
     *
     * @throws Exception If there was a problem stopping the container.
     * @throws IllegalStateException If the embedder has not been
     * started, or if its already been stopped.
     */
    public void stop()
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

    /**
     * Tries a variety of methods to find the configuration resource.
     *
     * BRW - I see this as fairly pointless as putting your config into the Embedder.class package
     *       will be annoying. Far better to just force the end user to provide a URL and remove
     *       all this logic.
     *
     * JVZ - What about uberjar applications?
     * BRW - If it doesn't work in uberjar, then we'll have a good test case and I'll fix classworlds.
     *
     * @return the stream containing the configuration
     * @throws RuntimeException when the configuration can not be found / opened
     */
    private InputStream findConfigurationInputStream()
    {
        if ( configurationURL != null )
        {
            try
            {
                return configurationURL.openStream();
            }
            catch ( IOException e )
            {
                throw new IllegalStateException( "The specified configuration resource cannot be found: " + configurationURL.toString() );
            }
        }

        InputStream is = getClass().getResourceAsStream( configuration );

        if ( is == null )
        {
            try
            {
                is = new FileInputStream( configuration );
            }
            catch ( FileNotFoundException e )
            {
                // do nothing.
            }
        }

        if ( is == null )
        {
            throw new IllegalStateException( "The specified configuration resource cannot be found: " + configuration );
        }

        return is;
    }
}
