package org.codehaus.plexus.service.repository.instance;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentManager;

/**
 * Manages a component instance.
 * Determines when a component is shutdown, and when it's started up. Each
 * instance deals with only one component class, though may handle multiple
 * instances of this class.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface InstanceManager
{
    static String ROLE = InstanceManager.class.getName();

  /* ComponentHousing newHousingInstance()
        throws Exception;
*/
    void configure( Configuration configuration )
        throws ConfigurationException;

    void initialize()
        throws Exception;

    void setClassLoader( ClassLoader classLoader );

    void setComponentImplementation( String implementation );

    void setComponentManager( ComponentManager manager );

    int getConnections();
    /**
     * Set the lifecycle handler to use. This is determined by the component.
     */
    void setLifecycleHandler(LifecycleHandler handler);
    /**
     * Dispose this manager. Instance manager should take any components it holds
     * through their shutdown lifecycle.
     *
     */
    void dispose();
    /**
     * Release the component back to this manager. The manager may decide to
     * end the components lifecycle, put it back in a pool, or just keep it alive. It can
     * be safely assumed the component is never null.
     *
     * @param component
     */
    void release(Object component);

    /**
     * Set this managers logger
     *
     * @param logger
     */
    void enableLogging(Logger logger);
    /**
     * Retrieve a component instance
     *
     * @return
     */
    Object getComponent() throws Exception;
}