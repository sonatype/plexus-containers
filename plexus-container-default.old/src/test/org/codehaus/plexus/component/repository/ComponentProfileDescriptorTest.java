package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;

/**
 */
public class ComponentProfileDescriptorTest
     extends TestCase
{
    public ComponentProfileDescriptorTest(String name)
    {
        super(name);
    }

    public void testComponentProfileDescriptor()
        throws Exception
    {
        ComponentProfileDescriptor d = new ComponentProfileDescriptor();

        d.setComponentFactoryId( "cfid" );

        assertEquals( "cfid", d.getComponentFactoryId() );

        d.setLifecycleHandlerId( "lfhid" );

        assertEquals( "lfhid", d.getLifecycleHandlerId() );

        d.setComponentManagerId( "cmid" );

        assertEquals( "cmid", d.getComponentManagerId() );
    }
}
