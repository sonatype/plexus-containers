package org.codehaus.plexus.component.composition.setter;

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test that component assembly works when setters are needed to satisfy requirements.
 *
 * @author Jason van Zyl
 */
public class SetterCompositionTest
    extends PlexusTestCase
{
    public void testSetterComposition()
        throws Exception
    {
        Component c = (Component) lookup( Component.ROLE );

        assertNotNull( "Requirement of ComponentA not composed.", c.getComponentA() );

        assertNotNull( "Requirement of ComponentB not composed.", c.getComponentB() );
    }
}
