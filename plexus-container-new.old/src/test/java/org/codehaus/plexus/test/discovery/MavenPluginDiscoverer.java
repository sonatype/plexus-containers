package org.codehaus.plexus.test.discovery;

import org.codehaus.plexus.component.discovery.AbstractComponentDiscoverer;
import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class MavenPluginDiscoverer
    extends AbstractComponentDiscoverer
{
    private PlexusXStream xstream;

    public MavenPluginDiscoverer()
    {
        xstream = new PlexusXStream();

        xstream.alias( "plugin", MavenPluginDescriptor.class );

        xstream.alias( "goal", GoalDescriptor.class );
    }

    public String getComponentDescriptorLocation()
    {
        return "META-INF/maven/plugin.xml";
    }

    public String getComponentType()
    {
        return "maven-plugin";
    }

    public List createComponentDescriptors( Reader componentDescriptorReader, String source )
        throws Exception
    {
        List componentDescriptors = new ArrayList();

        MavenPluginDescriptor pluginDescriptor = (MavenPluginDescriptor) xstream.build( componentDescriptorReader, MavenPluginDescriptor.class );

        componentDescriptors.add( pluginDescriptor );

        return componentDescriptors;
    }
}
