package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;
import org.codehaus.plexus.configuration.xstream.XStreamTool;

import java.io.StringReader;
import java.io.Reader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusTools
{
    public static ComponentDescriptor buildComponentDescriptor( String configuration )
        throws Exception
    {
        return buildComponentDescriptor( buildConfiguration( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( PlexusConfiguration configuration )
        throws Exception
    {
        XStreamTool xstreamTool = new XStreamTool();

        xstreamTool.alias( "component", ComponentDescriptor.class );

        xstreamTool.alias( "requirement", ComponentRequirement.class );

        ComponentDescriptor cd = (ComponentDescriptor) xstreamTool.build( configuration, ComponentDescriptor.class );

        return cd;
    }

    public static PlexusConfiguration buildConfiguration( Reader configuration )
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        return builder.parse( configuration );
    }

    public static PlexusConfiguration buildConfiguration( String configuration )
        throws Exception
    {
        return buildConfiguration( new StringReader( configuration ) );
    }
}
