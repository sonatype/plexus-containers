package org.codehaus.plexus.component.discovery;

import java.util.List;

import junit.framework.TestCase;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDiscovererTest
    extends TestCase
{
    public void testDefaultComponentDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        componentDiscoverer.setManager( new DefaultComponentDiscovererManager() );

        ComponentSetDescriptor componentSet = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        List components = componentSet.getComponents();

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.component.discovery.DiscoveredComponent", cd.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent", cd.getImplementation() );
    }
}
