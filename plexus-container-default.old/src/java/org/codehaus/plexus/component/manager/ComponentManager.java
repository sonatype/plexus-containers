package org.codehaus.plexus.component.manager;


import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 * Manages a component manager.
 * Determines when a component is shutdown, and when it's started up. Each
 * manager deals with only one component class, though may handle multiple
 * instances of this class.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentManager
{
    ComponentManager copy();

    static String ROLE = ComponentManager.class.getName();

    String getId();

    void setup( Logger logger, ClassLoader cl, LifecycleHandler h, ComponentDescriptor d )
        throws Exception;

    void initialize()
        throws Exception;

    int getConnections();

    ComponentDescriptor getComponentDescriptor();

    LifecycleHandler getLifecycleHandler();

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
    void release( Object component );

    void suspend( Object component );

    void resume( Object component );

    /**
     * Retrieve a component manager
     *
     * @return
     */
    Object getComponent()
        throws Exception;
}