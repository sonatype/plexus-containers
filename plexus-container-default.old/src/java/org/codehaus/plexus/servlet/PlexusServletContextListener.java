package org.codehaus.plexus.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.lifecycle.avalon.AvalonServiceManager;

/**
 * By adding this to the listeners for your web application, a Plexus container
 * will be instantiated and added to the attributes of the ServletContext.
 * 
 * @author <a href="bwalding@apache.org">Ben Walding</a>
 * @version $Id$
 */
public class PlexusServletContextListener implements ServletContextListener
{
    private PlexusContainer container = null;
    private static final String PLEXUSCONFIG = "/WEB-INF/plexus.xml";
    public static final String PLEXUS_CONTAINER = "plexus.container";
    public static final String PLEXUS_SERVICE_MANAGER = "plexus.service.manager";

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();

        context.log("Initializing Plexus container...");
        InputStream is = null;
        try
        {
            is = context.getResourceAsStream(PLEXUSCONFIG);
            if (is == null)
            {
                throw new RuntimeException(PLEXUSCONFIG + " not found");
            }
            container = createContainer(is);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    //Don't care
                }
            }
        }

        context.setAttribute(PLEXUS_CONTAINER, container);
        context.setAttribute(PLEXUS_SERVICE_MANAGER, new AvalonServiceManager(container.getComponentRepository()));
        context.log("Plexus container initialized.");
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();
        if (container != null)
        {
            context.log("Disposing of Plexus container.");
            try
            {
                container.dispose();
            }
            catch (Exception e)
            {
                context.log("Trying to dispose of Plexus container", e);
            }
        }
        context.removeAttribute(PLEXUS_CONTAINER);
        context.removeAttribute(PLEXUS_SERVICE_MANAGER);
    }

    /**
     * 
     * @param is
     * @return
     */
    private PlexusContainer createContainer(InputStream is)
    {
        PlexusContainer newContainer = new DefaultPlexusContainer();
        try
        {
            Reader config = new InputStreamReader(is);
            newContainer.setConfigurationResource(config);
            newContainer.initialize();
            newContainer.start();
            return newContainer;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not start Plexus!", e);
        }
    }

}
