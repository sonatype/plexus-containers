package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

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
    public String getComponentDescriptorLocation()
    {
        return "META-INF/maven/plugin.xml";
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

    public List createComponentDescriptors( PlexusConfiguration componentDescriptorConfiguration )
        throws Exception
    {
        List componentDescriptors = new ArrayList();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        // The role will be the same for all maven plugins.
        componentDescriptor.setRole( "org.apache.maven.plugin.Plugin" );

        // The id of the plugin will be our role hint.
        componentDescriptor.setRoleHint( componentDescriptorConfiguration.getChild( "id" ).getValue() );

        // The implemenation is specified.
        componentDescriptor.setImplementation( componentDescriptorConfiguration.getChild( "implementation" ).getValue() );

        componentDescriptors.add( componentDescriptor );

        return componentDescriptors;
    }
}
