package org.codehaus.plexus.component.composition.setter;

import org.codehaus.plexus.component.composition.ComponentA;
import org.codehaus.plexus.component.composition.ComponentB;

/**
 * @author Jason van Zyl
 */
public interface Component
{
    String ROLE = Component.class.getName();

    ComponentA getComponentA();

    ComponentB getComponentB();
}
