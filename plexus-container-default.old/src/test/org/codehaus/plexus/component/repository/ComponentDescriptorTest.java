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

    /**
     * The JUnit setup method
     */
    protected void setUp()
        throws Exception
    {
    }

    /**
     * The teardown method for JUnit
     */
    protected void tearDown()
        throws Exception
    {
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

        assertEquals( role, componentDescriptor.getRole() );
        assertEquals( roleHint, componentDescriptor.getRoleHint() );
        assertEquals( instantiation, componentDescriptor.getInstantiationStrategy() );
        assertEquals( role + roleHint, componentDescriptor.getComponentKey() );
    }
}
