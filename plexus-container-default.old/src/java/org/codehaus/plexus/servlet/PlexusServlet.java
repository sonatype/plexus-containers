package org.codehaus.plexus.servlet;

import org.apache.avalon.framework.service.ServiceManager;

import javax.servlet.http.HttpServlet;

public class PlexusServlet extends HttpServlet
{
    public static final String SERVICE_MANAGER_KEY = "plexus.component.manager";

    public ServiceManager getServiceManager()
    {
        return (ServiceManager) getServletContext().getAttribute( SERVICE_MANAGER_KEY );
    }
}
