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

    // ----------------------------------------------------------------------------------
    // <plugin>
    //   <implementation>org.apache.maven.plugin.AntlrPlugin</implementation>
    //   <id>antlr</id>
    //   <goals>
    //     <goal>
    //       <name>antlr</name>
    //       <configuration>
    //         <outputDirectory>#maven.build.dest</outputDirectory>
    //         <resources>#project.build.resources</resources>
    //       </configuration>
    //     </goal>
    //   </goals>
    // </plugin>
    // ----------------------------------------------------------------------------------

    public List createComponentDescriptors( Reader componentDescriptorReader )
        throws Exception
    {
        List componentDescriptors = new ArrayList();

        MavenPluginDescriptor pluginDescriptor = (MavenPluginDescriptor) xstream.build( componentDescriptorReader, MavenPluginDescriptor.class );

        componentDescriptors.add( pluginDescriptor );

        return componentDescriptors;
    }
}
