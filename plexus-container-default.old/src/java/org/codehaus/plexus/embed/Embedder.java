package org.codehaus.plexus.embed;

import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        container.setConfigurationResource(
            new InputStreamReader( findConfigurationInputStream() ) );
        container.initialize();
        container.start();
        embedderStarted = true;
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
        if ( !embedderStarted )
        {
            throw new IllegalStateException( "Embedder not started" );
        }

        if ( embedderStopped )
        {
            throw new IllegalStateException( "Embedder already stopped" );
        }

        container.dispose();
        embedderStarted = false;
        embedderStopped = true;
    }

    public void run()
        throws Exception
    {
        start();
        stop();
    }

    private InputStream findConfigurationInputStream()
    {
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
