package org.codehaus.plexus.servlet;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;

/**
 * <code>ServletContextUtils</code> provides methods to embed a Plexus
 * container within a Servlet context.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
final class ServletContextUtils {
    // prevent instantiation
    private ServletContextUtils() {
    }

    static Embedder createContainer(ServletContext context, String plexusConf)
        throws ServletException
    {
        Embedder embedder;
        File f;
        PlexusContainer plexus;
        ServiceManager serviceManager;
    
        embedder = new Embedder();
        f = new File( context.getRealPath( "/WEB-INF" ) );
        embedder.addContextValue( "plexus.home", f.getAbsolutePath() );
        f = new File( context.getRealPath( plexusConf ) );
        embedder.setConfiguration( f.getAbsolutePath() );
        try
        {
            embedder.start();
        }
        catch ( Exception e )
        {
            throw new ServletException( "Could not start Plexus!", e );
        }
        plexus = embedder.getContainer();
        context.setAttribute( PlexusConstants.PLEXUS_KEY, plexus );
        serviceManager = new AvalonServiceManager( plexus.getComponentRepository() );
        context.setAttribute( PlexusConstants.SERVICE_MANAGER_KEY, serviceManager );
        return embedder;
    }

    static void destroyContainer(Embedder embedder, ServletContext context)
    {
        try
        {
            if ( embedder != null )
            {
                embedder.stop();
            }
        }
        catch (Exception e)
        {
            context.log("Trying to dispose of Plexus container", e);
        }
        finally
        {
            context.removeAttribute( PlexusConstants.PLEXUS_KEY );
            context.removeAttribute( PlexusConstants.SERVICE_MANAGER_KEY );
        }
    }
}
