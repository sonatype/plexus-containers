package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.PlexusContainer;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class PlexusServletContextListenerTest extends TestCase
{
    public void testSimple()
    {
        ServletContext sc = new MockServletContext();

        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent(sc);
        
        pacl.contextInitialized(sce);
        {
            PlexusContainer pc = (PlexusContainer) sc.getAttribute(PlexusServletContextListener.PLEXUS_CONTAINER);
            ServiceManager sm = (ServiceManager) sc.getAttribute(PlexusServletContextListener.PLEXUS_SERVICE_MANAGER);
            assertNotNull("pc", pc);
            assertNotNull("sm", sm);
        }

        pacl.contextDestroyed(sce);
        {
            PlexusContainer pc = (PlexusContainer) sc.getAttribute(PlexusServletContextListener.PLEXUS_CONTAINER);
            ServiceManager sm = (ServiceManager) sc.getAttribute(PlexusServletContextListener.PLEXUS_SERVICE_MANAGER);
            assertNull("pc", pc);
            assertNull("sm", sm);
        }
    }
}
