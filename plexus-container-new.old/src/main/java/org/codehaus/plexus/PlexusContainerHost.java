package org.codehaus.plexus;

import com.werken.classworlds.ClassWorld;

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
    private boolean shouldStop;
    private boolean isStopped;

    // ----------------------------------------------------------------------
    //  Constructors
    // ----------------------------------------------------------------------

    /**
     *  Constuctor.
     */
    public PlexusContainerHost()
    {
    }

    // ----------------------------------------------------------------------
    //  Implementation
    // ----------------------------------------------------------------------

    public void start( ClassWorld classWorld, String configurationResource )
        throws Exception
    {
        PlexusContainer container = new DefaultPlexusContainer();
        container.setClassWorld( classWorld );
        container.setConfigurationResource( new FileReader( configurationResource ) );

        container.addContextValue( "plexus.home",
                                   System.getProperty( "plexus.home" ) );

        container.addContextValue( "plexus.work",
                                   System.getProperty( "plexus.home" ) + "/work" );

        container.addContextValue( "plexus.logs",
                                   System.getProperty( "plexus.home" ) + "/logs" );

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
    }

    /**
     *  Asynchronous hosting component loop.
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
                    break;
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
    //  Startup
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
            notifyAll();
        }

        synchronized ( this )
        {
            while ( !isStopped )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    break;
                }
            }
        }
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
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 2 );
        }
    }
}

