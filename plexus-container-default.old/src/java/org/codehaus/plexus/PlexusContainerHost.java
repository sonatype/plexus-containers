package org.codehaus.plexus;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * ----------------------------------------------------------------------------
 */

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
     *  Asynchronous hosting service loop.
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

