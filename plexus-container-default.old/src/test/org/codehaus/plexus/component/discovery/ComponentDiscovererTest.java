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
    public void testDefaultComponentDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        List components = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.discovery.DiscoveredComponent", cd.getRole() );

        assertEquals( "org.codehaus.plexus.discovery.DefaultDiscoveredComponent", cd.getImplementation() );
    }

    public void testMavenPluginDiscoverer()
    {
        ComponentDiscoverer componentDiscoverer = new MavenPluginDiscoverer();

        List components = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.apache.maven.plugin.Plugin", cd.getRole() );

        assertEquals( "org.apache.maven.plugin.AntlrPlugin", cd.getImplementation() );
    }
}
