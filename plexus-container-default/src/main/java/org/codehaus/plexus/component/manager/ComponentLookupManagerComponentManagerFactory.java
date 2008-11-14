package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.ComponentLookupManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

public class ComponentLookupManagerComponentManagerFactory implements ComponentManagerFactory
{
    public String getId()
    {
        return "component-lookup-manager";
    }

    public ComponentManager<ComponentLookupManager> createComponentManager( MutablePlexusContainer container,
                                                    LifecycleHandler lifecycleHandler,
                                                    ComponentDescriptor componentDescriptor,
                                                    String role,
                                                    String roleHint )
        throws UndefinedComponentManagerException
    {
        return new ComponentLookupManagerComponentManager( container, lifecycleHandler, componentDescriptor, role, roleHint );
    }
}
