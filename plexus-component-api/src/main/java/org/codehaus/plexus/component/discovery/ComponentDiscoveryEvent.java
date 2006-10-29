package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

public class ComponentDiscoveryEvent
{
    private ComponentSetDescriptor componentSetDescriptor;

    public ComponentDiscoveryEvent( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    public ComponentSetDescriptor getComponentSetDescriptor()
    {
        return componentSetDescriptor;
    }
}
