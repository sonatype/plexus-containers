package org.codehaus.plexus;

import org.codehaus.classworlds.ClassWorld;

import java.io.File;
import java.io.FileReader;

/**
 * A <code>ContainerHost</code>.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 * @version $Id$
 */
public class PlexusContainerHost
    implements Runnable
{
    private DefaultPlexusContainer container;

    private boolean shouldStop;

    private boolean isStopped;

    private Object shutdownSignal;
    
    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    /**
     *  Constuctor.
     */
    public PlexusContainerHost()
    {
        shutdownSignal = new Object();
    }

    // ----------------------------------------------------------------------
    //  Implementation
    // ----------------------------------------------------------------------

    public PlexusContainer start( ClassWorld classWorld, String configurationResource )
        throws Exception
    {
        container = getPlexusContainer();

        container.setClassWorld( classWorld );
        container.setConfigurationResource( new FileReader( configurationResource ) );

        customizeContainer( container );

        // Move this to the logging subsystem. And there might be a logging directory
        // so we need better analsys as the container might be embedded.
        File plexusLogs = new File( System.getProperty( "plexus.home" ) + "/logs" );
        if ( plexusLogs.exists() == false )
        {
            plexusLogs.mkdirs();
        }

        container.initialize();
        container.start();

        Thread thread = new Thread( this );

        thread.setDaemon( false );

        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    shutdown();
                }
                catch ( Exception e )
                {
                    // do nothing.
                }
            }
        } ) );

        thread.start();

        return container;
    }

    // ----------------------------------------------------------------------
    // Methods for customizing the container
    // ----------------------------------------------------------------------

    protected DefaultPlexusContainer getPlexusContainer()
    {
        return new DefaultPlexusContainer();
    }

    protected void customizeContainer( PlexusContainer container )
    {
        container.addContextValue( "plexus.home", System.getProperty( "plexus.home" ) );

        container.addContextValue( "plexus.work", System.getProperty( "plexus.home" ) + "/work" );

        container.addContextValue( "plexus.logs", System.getProperty( "plexus.home" ) + "/logs" );
    }

    /**
     * Asynchronous hosting component loop.
     */
    public void run()
    {
        synchronized ( this )
        {
            while ( !shouldStop )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    //ignore
                }
            }
        }

        synchronized ( this )
        {
            isStopped = true;
            notifyAll();
        }
    }

    // ----------------------------------------------------------------------
    //  Container control
    // ----------------------------------------------------------------------

    /**
     * Shutdown this container.
     *
     * @throws java.lang.Exception If an error occurs while shutting down the container.
     */
    public void shutdown()
        throws Exception
    {
        synchronized ( this )
        {
            shouldStop = true;

            container.dispose();

            notifyAll();
        }

        synchronized ( this )
        {
            while ( !isStopped() )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    //ignore
                }
            }

            synchronized( shutdownSignal )
            {
                shutdownSignal.notifyAll();
            }
        }
    }

    public void waitForContainerShutdown()
    {
        while ( !isStopped() )
        {
            try
            {
                synchronized( shutdownSignal )
                {
                    shutdownSignal.wait();
                }
            }
            catch ( InterruptedException e )
            {
                // ignored
            }
        }
    }

    public boolean isStopped()
    {
        return isStopped;
    }

    /**
     *  Main entry-point.
     *
     *  @param args Command-line arguments.
     */
    public static void main( String[] args, ClassWorld classWorld )
    {
        if ( args.length != 1 )
        {
            System.err.println( "usage: plexus <plexus.conf>" );
            System.exit( 1 );
        }

        try
        {
            PlexusContainerHost host = new PlexusContainerHost();

            host.start( classWorld, args[0] );

            host.waitForContainerShutdown();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 2 );
        }
    }
}
