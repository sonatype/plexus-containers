package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.avalon.framework.service.ServiceManager;

import org.codehaus.plexus.PlexusConstants;
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
            PlexusContainer pc = (PlexusContainer) sc.getAttribute( PlexusConstants.PLEXUS_KEY );
            ServiceManager sm = (ServiceManager) sc.getAttribute( PlexusConstants.SERVICE_MANAGER_KEY );
            assertNotNull("pc", pc);
            assertNotNull("sm", sm);
        }

        pacl.contextDestroyed(sce);
        {
            PlexusContainer pc = (PlexusContainer) sc.getAttribute( PlexusConstants.PLEXUS_KEY );
            ServiceManager sm = (ServiceManager) sc.getAttribute( PlexusConstants.SERVICE_MANAGER_KEY );
            assertNull("pc", pc);
            assertNull("sm", sm);
        }
    }

    /**
     * Test the static methods.
     */
    public void testStaticMethods()
    {
        ServletContext sc = new MockServletContext();
        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent(sc);

        pacl.contextInitialized( sce );
        assertNotNull( PlexusServletContextListener.getServiceManager( sc ) );
        assertNotNull( PlexusServletContextListener.getPlexusContainer( sc ) );

        pacl.contextDestroyed( sce );
        assertNull( PlexusServletContextListener.getServiceManager( sc ) );
        assertNull( PlexusServletContextListener.getPlexusContainer( sc ) );
    }
}
