package org.codehaus.plexus.servlet;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * <p>PlexusLoaderServlet loads a Plexus manager for a web application.  The
 * Plexus <code>ServiceBroker</code> manager is put into the
 * <code>ServletContext</code> so <code>PlexusServlet</code>s can retrieve it.
 * It is important to make sure that this class is loaded before the startup of
 * your web application that uses the Plexus <code>ServiceBroker</code>.
 * Alternatively, the servlet container can be loaded as a component within a
 * Plexus manager.  In that case, this class is no longer needed.
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
    /** Plexus manager */
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
