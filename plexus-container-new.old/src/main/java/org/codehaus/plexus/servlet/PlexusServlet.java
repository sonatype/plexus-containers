package org.codehaus.plexus.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusConstants;

/**
 * Extension of {@link HttpServlet} that provides convenience methods for
 * servlets that need to access services provided by a Plexus container
 * embedded in a Servlet context. The Plexus container can be created using
 * either {@link PlexusLoaderServlet} or {@link PlexusServletContextListener}
 * as part of a web application's configuration. Alternatively the servlet
 * container may be a Plexus component, as with the Plexus jetty component.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public abstract class PlexusServlet extends HttpServlet
{
    /** @deprecated */
    public static final String SERVICE_MANAGER_KEY = PlexusConstants.SERVICE_MANAGER_KEY;

    /** @deprecated */
    public final ServiceManager getServiceManager()
    {
        return PlexusServletUtils.getServiceManager( getServletContext() );
    }

    public final boolean hasService(String role) throws ServletException
    {
        return PlexusServletUtils.hasService( getServletContext(), role );
    }

    public final boolean hasService(String role, String id)
        throws ServletException
    {
        return PlexusServletUtils.hasService( getServletContext(), role, id );
    }

    public final Object lookup(String role) throws ServletException
    {
        return PlexusServletUtils.lookup( getServletContext(), role );
    }

    public final Object lookup(String role, String id)
        throws ServletException
    {
        return PlexusServletUtils.lookup( getServletContext(), role, id );
    }

    public final void release(Object service) throws ServletException {
        PlexusServletUtils.release( getServletContext(), service );
    }
}
