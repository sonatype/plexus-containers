package org.codehaus.plexus.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.codehaus.plexus.embed.Embedder;

/**
 * <p>PlexusLoaderServlet loads a Plexus {@link Embedder} for a web
 * application.  The embedder is put into the
 * <code>ServletContext</code> so <code>PlexusServlet</code>s can retrieve it.
 * It is important to make sure that this class is loaded before the startup of
 * your web application that uses the Plexus <code>Embedder</code>.
 * Alternatively, the servlet container can be loaded as a component within a
 * Plexus manager.  In that case, this class is no longer needed.
 * </p>
 * <p>
 * To configure this servlet you must specify the location of the plexus
 * configuration file relative to the root of your webapplication.  This must be
 * passed as the "plexus-config" init-parameter to the servlet.
 * </p>
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mhw@kremvax.net@>Mark Wilkinson</a>
 * @since Feb 2, 2003
 */
public class PlexusLoaderServlet extends HttpServlet
{
    /** Embedded Plexus. */
    private Embedder embedder;

    /**
     * Load Plexus into the ServletContext for PlexusServlets.
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        super.init();

        log( "Initializing Plexus..." );
        String configName = getInitParameter( ServletContextUtils.PLEXUS_CONFIG_PARAM );

        embedder = ServletContextUtils.createContainer( getServletContext(),
                                                        configName );
        log( "Plexus Initialized." );
    }

    /**
     * Shutdown plexus.
     *
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
        log( "Shutting down Plexus..." );
        ServletContextUtils.destroyContainer( embedder, getServletContext() );
        log( "... Plexus shutdown. Goodbye" );

        super.destroy();
    }
}
