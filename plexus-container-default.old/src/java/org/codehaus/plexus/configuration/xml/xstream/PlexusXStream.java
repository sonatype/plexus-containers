package org.codehaus.plexus.configuration.xml.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.objecttree.reflection.JavaReflectionObjectFactory;
import com.thoughtworks.xstream.xml.xpp3.Xpp3DomXMLReader;
import com.thoughtworks.xstream.xml.xpp3.Xpp3DomXMLReaderDriver;
import com.thoughtworks.xstream.xml.xpp3.Xpp3DomXMLWriter;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSet;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.alias.HyphenatedClassMapper;
import org.codehaus.plexus.configuration.xml.xstream.alias.HyphenatedNameMapper;
import org.codehaus.plexus.configuration.xml.xstream.converters.PlexusConfigurationConverter;
import org.codehaus.plexus.configuration.xml.xstream.converters.PropertiesConverter;

import java.io.Reader;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PlexusXStream
    extends XStream
{
    public PlexusXStream()
    {
        super( new JavaReflectionObjectFactory(), new HyphenatedClassMapper( new HyphenatedNameMapper() ), new Xpp3DomXMLReaderDriver(), "implementation" );

        alias( "configuration", PlexusConfiguration.class, XmlPlexusConfiguration.class );

        alias( "component-set", ComponentSet.class );

        alias( "component", ComponentDescriptor.class );

        alias( "requirement", ComponentRequirement.class );

        alias( "dependency", ComponentDependency.class );

        registerConverter( new PlexusConfigurationConverter() );

        registerConverter( new PropertiesConverter() );
    }

    public Object build( Reader reader, Class clazz )
        throws Exception
    {
        return build( PlexusTools.buildConfiguration( reader ), clazz );
    }

    public Object build( PlexusConfiguration configuration )
        throws Exception
    {
        Xpp3DomXMLReader reader = new Xpp3DomXMLReader( ( (XmlPlexusConfiguration) configuration ).getXpp3Dom() );

        Object object = fromXML( reader );

        return object;
    }

    public Object build( PlexusConfiguration configuration, Class clazz )
        throws Exception
    {
        Xpp3DomXMLReader reader = new Xpp3DomXMLReader( ( (XmlPlexusConfiguration) configuration ).getXpp3Dom() );

        alias( "basePackage", clazz );

        Object object = fromXML( reader );

        return object;
    }

    public Object build( PlexusConfiguration configuration, Object root )
        throws Exception
    {
        Xpp3DomXMLReader reader = new Xpp3DomXMLReader( ( (XmlPlexusConfiguration) configuration ).getXpp3Dom() );

        alias( "basePackage", root.getClass() );

        Object object = fromXML( reader, root );

        return object;
    }

    // Write xml configuration
    public XmlPlexusConfiguration write( Object o )
        throws Exception
    {
        Xpp3DomXMLWriter writer = new Xpp3DomXMLWriter();

        toXML( o, writer );

        return new XmlPlexusConfiguration( writer.getConfiguration() );
    }
}
