package org.codehaus.plexus.component.manager;


import org.codehaus.plexus.PlexusContainer;
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
    String ROLE = ComponentManager.class.getName();

    ComponentManager copy();

    String getId();

    void setup( PlexusContainer container, LifecycleHandler lifecycleHandler, ComponentDescriptor componentDescriptor )
        throws Exception;

    void initialize()
        throws Exception;

    int getConnections();

    LifecycleHandler getLifecycleHandler();

    void dispose()
        throws Exception;

    void release( Object component )
        throws Exception;

    void suspend( Object component )
        throws Exception;

    void resume( Object component )
        throws Exception;

    Object getComponent()
        throws Exception;

    ComponentDescriptor getComponentDescriptor();

    PlexusContainer getContainer();
}