package org.codehaus.plexus.component.discovery;

import junit.framework.TestCase;

import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDiscovererTest
    extends TestCase
{
    public void testComponentDiscovery()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        List components = componentDiscoverer.findComponents( null );

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.Foo", cd.getRole() );

        assertEquals( "org.codehaus.plexus.DefaultFoo", cd.getImplementation() );
    }
}
