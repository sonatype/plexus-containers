package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;

/**
 */
public class ComponentDescriptorTest
     extends TestCase
{
    public ComponentDescriptorTest(String name)
    {
        super(name);
    }

    public void testComponentDescriptor()
        throws Exception
    {
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        String role = "org.codehaus.plexus.Foo";

        String roleHint = "role-hint";

        String instantiation = "singleton";


        componentDescriptor.setRole( role );

        componentDescriptor.setRoleHint( roleHint );

        componentDescriptor.setInstantiationStrategy( instantiation );

        componentDescriptor.addRequirement( "foo" );

        componentDescriptor.setComponentProfile( "profile" );


        assertEquals( role, componentDescriptor.getRole() );

        assertEquals( roleHint, componentDescriptor.getRoleHint() );

        assertEquals( instantiation, componentDescriptor.getInstantiationStrategy() );

        assertEquals( role + roleHint, componentDescriptor.getComponentKey() );

        assertTrue( componentDescriptor.getRequirements().contains( "foo" ) );

        assertEquals( "profile", componentDescriptor.getComponentProfile() );
    }
}
