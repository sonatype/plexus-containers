package org.codehaus.plexus.component.composition;

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
