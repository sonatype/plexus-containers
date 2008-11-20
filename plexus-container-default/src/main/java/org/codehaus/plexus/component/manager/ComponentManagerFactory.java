package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

public interface ComponentManagerFactory
{
    /**
     * Gets the unique identifier of this ComponentManagerFactory.  This id is the instantiation strategy specified
     * in a component descriptor.
     * @return the unique identifier and instantiation strategy name
     */
    String getId();

    /**
     * Creates a new component manager for the specified component descriptor.
     */
    <T> ComponentManager<T> createComponentManager( MutablePlexusContainer container,
                                             LifecycleHandler lifecycleHandler,
                                             ComponentDescriptor<T> componentDescriptor,
                                             String role,
                                             String roleHint );
}
