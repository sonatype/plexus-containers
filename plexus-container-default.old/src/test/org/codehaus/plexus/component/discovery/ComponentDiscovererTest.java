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

        List components = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.component.discovery.DiscoveredComponent", cd.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent", cd.getImplementation() );
    }

    public void testMavenPluginDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new MavenPluginDiscoverer();

        List components = componentDiscoverer.findComponents( Thread.currentThread().getContextClassLoader() );

        MavenPluginDescriptor pluginDescriptor = (MavenPluginDescriptor) components.get( 0 );

        assertEquals( "org.apache.maven.plugin.Plugin", pluginDescriptor.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.MockMavenPlugin", pluginDescriptor.getImplementation() );

        assertEquals( 1, pluginDescriptor.getGoals().size() );

        GoalDescriptor goalDescriptor = (GoalDescriptor) pluginDescriptor.getGoals().get( 0 );

        assertEquals( "antlr", goalDescriptor.getName() );

        PlexusConfiguration c = goalDescriptor.getConfiguration();

        assertEquals( "#maven.build.dest", c.getChild( "outputDirectory" ).getValue() );
    }
}
