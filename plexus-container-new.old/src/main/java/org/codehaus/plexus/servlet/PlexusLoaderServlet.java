package org.codehaus.plexus.servlet;

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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * <p>PlexusLoaderServlet loads a Plexus instance for a web application.  The
 * Plexus <code>ServiceBroker</code> instance is put into the
 * <code>ServletContext</code> so <code>PlexusServlet</code>s can retrieve it.
 * It is important to make sure that this class is loaded before the startup of
 * your web application that uses the Plexus <code>ServiceBroker</code>.
 * Alternatively, the servlet container can be loaded as a component within a
 * Plexus instance.  In that case, this class is no longer needed.
 * </p>
 * <p>
 * To configure this servlet you must specify the location of the plexus
 * configuration file relative to the root of your webapplication.  This must be
 * passed as the "plexus-config" init-parameter to the servlet.
 * </p>
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 2, 2003
 */
public class PlexusLoaderServlet extends HttpServlet
{
    /** Plexus instance */
    private DefaultPlexusContainer container;

    /**
     * Load Plexus into the ServletContext for PlexusServlets.
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();

        log( "Initializing Plexus..." );
        String configFileName = getInitParameter( "plexus-config" );
        String applicationRoot = getServletContext().getRealPath( "" );

        System.getProperties().setProperty( "plexus.home", applicationRoot + "/WEB-INF" );

        File configuration = new File( applicationRoot, configFileName );
        Reader config = null;
        try
        {
            config = new FileReader( configuration.getAbsolutePath() );
        }
        catch ( FileNotFoundException e )
        {
            throw new ServletException( "Could not find the Plexus configuration!", e );
        }

        // Start plexus
        container = new DefaultPlexusContainer();
        try
        {
            container.addContextValue( "plexus.home", applicationRoot + "/WEB-INF" );
            container.setConfigurationResource( config );
            container.initialize();
            container.start();
        }
        catch ( Exception e )
        {
            throw new ServletException( "Could not start Plexus!", e );
        }

        // Put the ServiceBroker in the context
        this.getServletContext().setAttribute( PlexusServlet.SERVICE_MANAGER_KEY,
                                               new AvalonServiceManager( container.getComponentRepository() ) );
        log( "Plexus Initializied." );
    }

    /**
     * Shutdown plexus.
     *
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
        try
        {
            log( "Shutting down plexus!..." );
            container.dispose();
            log( "...plexus shutdown. goodbye" );
        }
        catch ( Exception e )
        {
            this.log( "Could not shutdown Plexus!", e );
        }

        super.destroy();
    }
}
