package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class ComponentDiscoveryEvent
{
    private ComponentDescriptor componentDescriptor;

    private String componentType;

    public ComponentDiscoveryEvent( ComponentDescriptor componentDescriptor, String componentType )
    {
        this.componentDescriptor = componentDescriptor;

        this.componentType = componentType;
    }

    public ComponentDescriptor getComponentDescriptor()
    {
        return componentDescriptor;
    }

    public String getComponentType()
    {
        return componentType;
    }
}
