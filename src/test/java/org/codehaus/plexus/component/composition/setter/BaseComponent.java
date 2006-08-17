package org.codehaus.plexus.component.composition.setter;

import org.codehaus.plexus.component.composition.ComponentA;

/**
 * @author Jason van Zyl
 */
public class BaseComponent
{
    private ComponentA componentA;

    public ComponentA getComponentA()
    {
        return componentA;
    }

    public void setComponentA( ComponentA componentA )
    {
        this.componentA = componentA;
    }
}
