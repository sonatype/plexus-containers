package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

public class PerLookupComponentManagerFactory implements ComponentManagerFactory
{
    public String getId()
    {
        return "per-lookup";
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    public <T> ComponentManager<T> createComponentManager( MutablePlexusContainer container,
                                                       LifecycleHandler lifecycleHandler,
                                                       ComponentDescriptor<T> componentDescriptor )
    {
        return new PerLookupComponentManager( container, lifecycleHandler, componentDescriptor );
    }
}
