package org.codehaus.plexus.configuration.xml.xstream;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.io.Reader;
import java.io.StringReader;

import com.thoughtworks.xstream.xml.xpp3.Xpp3DomBuilder;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 *
 * @todo these are all really tools for dealing with xml configurations so they
 * should be packaged as such.
 */
public class PlexusTools
{
    private static PlexusXStream xstream = new PlexusXStream();

    public static ComponentDescriptor buildComponentDescriptor( String configuration )
        throws Exception
    {
        return buildComponentDescriptor( buildConfiguration( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( PlexusConfiguration configuration )
        throws Exception
    {
        ComponentDescriptor cd = (ComponentDescriptor) xstream.build( configuration, ComponentDescriptor.class );

        return cd;
    }

    public static ComponentSetDescriptor buildComponentSet( PlexusConfiguration configuration )
        throws Exception
    {
        ComponentSetDescriptor cs = (ComponentSetDescriptor) xstream.build( configuration, ComponentSetDescriptor.class );

        return cs;
    }

    public static PlexusConfiguration buildConfiguration( Reader configuration )
        throws Exception
    {
        return new XmlPlexusConfiguration( Xpp3DomBuilder.build( configuration ) );
    }

    public static PlexusConfiguration buildConfiguration( String configuration )
        throws Exception
    {
        return buildConfiguration( new StringReader( configuration ) );
    }
}
