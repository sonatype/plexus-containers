package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

public class SingletonComponentManagerFactory implements ComponentManagerFactory
{
    public String getId()
    {
        return "singleton";
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    public ComponentManager<?> createComponentManager( MutablePlexusContainer container,
                                                    LifecycleHandler lifecycleHandler,
                                                    ComponentDescriptor componentDescriptor,
                                                    String role,
                                                    String roleHint )
    {
        return new SingletonComponentManager( container, lifecycleHandler, componentDescriptor, role, roleHint );
    }
}
