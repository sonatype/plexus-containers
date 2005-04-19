package org.codehaus.plexus.component.manager;


import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
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

    void setup( PlexusContainer container, LifecycleHandler lifecycleHandler, ComponentDescriptor componentDescriptor );

    void initialize();

    int getConnections();

    LifecycleHandler getLifecycleHandler();

    void dispose()
        throws ComponentLifecycleException;

    void release( Object component )
        throws ComponentLifecycleException;

    void suspend( Object component )
        throws ComponentLifecycleException;

    void resume( Object component )
        throws ComponentLifecycleException;

    Object getComponent()
        throws ComponentInstantiationException, ComponentLifecycleException;

    ComponentDescriptor getComponentDescriptor();

    PlexusContainer getContainer();
}