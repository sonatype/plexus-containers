/*
 * $Id$
 */

package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;

/**
 * A collection of static helper methods for code running within a Servlet
 * environment that needs to access an embedded Plexus container. Such code
 * can either extend {@link PlexusServlet} or invoke these static methods
 * directly.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class PlexusServletUtils {
    // prevent instantiation
    private PlexusServletUtils() {
    }

    /**
     * Get a reference to the Avalon service manager for the Plexus container
     * loaded into the <code>ServletContext</code>, if one exists.
     *
     * @param sc The servlet context that Plexus is installed in.
     * @return a <code>ServiceManager</code> object, or <code>null</code>
     * if none was registered in the servlet context.
     */
    public static ServiceManager getServiceManager(ServletContext sc)  {
        return (ServiceManager) sc.getAttribute( PlexusConstants.SERVICE_MANAGER_KEY );
    }

    /**
     * Get a reference to the Plexus container loaded into the
     * <code>ServletContext</code>, if one exists.
     *
     * @param sc The servlet context that Plexus is installed in.
     * @return a <code>PlexusContainer</code> object, or <code>null</code>
     * if none was registered in the servlet context.
     */
    public static PlexusContainer getPlexusContainer(ServletContext sc)  {
        return (PlexusContainer) sc.getAttribute( PlexusConstants.PLEXUS_KEY );
    }

    public static boolean hasService(ServletContext sc, String role)
        throws ServletException
    {
        return getServiceManager( sc ).hasService( role );
    }

    public static boolean hasService(ServletContext sc, String role, String id)
        throws ServletException
    {
        String selectorName;
        ServiceManager serviceManager;
        Object o;
        ServiceSelector selector;

        selectorName = role + PlexusConstants.SELECTOR_IMPL_SUFFIX;
        serviceManager = getServiceManager( sc );
        if ( !serviceManager.hasService( selectorName ) ) {
            return false;
        }
        try {
            o = serviceManager.lookup( selectorName );
            selector = (ServiceSelector) o;
            return selector.isSelectable(id);
        } catch (ServiceException e) {
            throw new ServletException("could not lookup service", e);
        }
    }

    public static Object lookup(ServletContext sc, String role)
        throws ServletException
    {
        try {
            return getServiceManager( sc ).lookup( role );
        } catch (ServiceException e) {
            throw new ServletException("could not lookup service", e);
        }
    }

    public static Object lookup(ServletContext sc, String role, String id)
        throws ServletException
    {
        String selectorName;
        ServiceManager serviceManager;
        Object o;
        ServiceSelector selector;

        selectorName = role + PlexusConstants.SELECTOR_IMPL_SUFFIX;
        serviceManager = getServiceManager( sc );
        try {
            o = serviceManager.lookup( selectorName );
            selector = (ServiceSelector) o;
            return selector.select( id );
        } catch (ServiceException e) {
            throw new ServletException("could not lookup service", e);
        }
    }

    public static void release(ServletContext sc, Object service)
        throws ServletException
    {
        getServiceManager( sc ).release(service);
    }
}
