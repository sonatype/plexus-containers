package org.codehaus.plexus.component.composition.setter;

import org.codehaus.plexus.component.composition.ComponentA;
import org.codehaus.plexus.component.composition.ComponentB;

/**
 * @author Jason van Zyl
 */
public class BaseComponent
    implements Component
{
    private ComponentA _componentA;

    private ComponentB _componentB;

    public ComponentA getComponentA()
    {
        return _componentA;
    }

    public void setComponentA( ComponentA componentA )
    {
        this._componentA = componentA;
    }

    public ComponentB getComponentB()
    {
        return _componentB;
    }

    public void setComponentB( ComponentB componentB )
    {
        this._componentB = componentB;
    }
}
