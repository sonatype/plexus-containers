package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;

/**
 */
public class ServiceDescriptorTest
     extends TestCase
{
    public ServiceDescriptorTest(String name)
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

    public void testServiceDescriptor()
        throws Exception
    {
        ComponentDescriptor sd = new ComponentDescriptor();

        String role = "org.codehaus.plexus.Foo";
        String id = "id";
        String instantiation = "singleton";

        sd.setRole( role );
        sd.setId( id );
        sd.setInstantiationStrategy( instantiation );

        assertEquals( role, sd.getRole() );
        assertEquals( id, sd.getId() );
        assertEquals( instantiation, sd.getInstantiationStrategy() );
        assertEquals( role + id, sd.getComponentKey() );
    }
}
