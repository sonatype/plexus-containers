package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSet;
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
    private static XStreamTool xstreamTool = new XStreamTool();

    public static ComponentDescriptor buildComponentDescriptor( String configuration )
        throws Exception
    {
        return buildComponentDescriptor( buildConfiguration( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( PlexusConfiguration configuration )
        throws Exception
    {
        ComponentDescriptor cd = (ComponentDescriptor) xstreamTool.build( configuration, ComponentDescriptor.class );

        return cd;
    }

    public static ComponentSet buildComponentSet( PlexusConfiguration configuration )
        throws Exception
    {
        ComponentSet cs = (ComponentSet) xstreamTool.build( configuration, ComponentSet.class );

        return cs;
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

    public static XStreamTool getXStream()
    {
        return xstreamTool;
    }
}
