package org.codehaus.plexus.component.discovery;

import junit.framework.TestCase;

import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

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
    public void testDefaultComponentDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        componentDiscoverer.setManager( new DefaultComponentDiscovererManager() );

        List components = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.component.discovery.DiscoveredComponent", cd.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent", cd.getImplementation() );
    }
}
