package org.codehaus.plexus.component.configurator;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.StringReader;
import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentConfiguratorTest
    extends TestCase
{
    public DefaultComponentConfiguratorTest( String s )
    {
        super( s );
    }

    public void testComponentConfigurator()
        throws Exception
    {
        String xml =
            "<component>" +
            "  <int-value>0</int-value>" +
            "  <float-value>1</float-value>" +
            "  <long-value>2</long-value>" +
            "  <double-value>3</double-value>" +
            "  <string-value>foo</string-value>" +
            "  <important-things>" +
            "    <important-thing><name>jason</name></important-thing>" +
            "    <important-thing><name>tess</name></important-thing>" +
            "  </important-things>" +
            "  <configuration>" +
            "    <name>jason</name>" +
            "  </configuration>" +
            "</component>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        ConfigurableComponent component = new ConfigurableComponent();

        ComponentConfigurator cc = new DefaultComponentConfigurator();

        cc.configureComponent( component, configuration );

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertEquals( "check long value", 2L, component.getLongValue() );

        assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertEquals( "foo", component.getStringValue() );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ((ImportantThing)list.get(0)).getName() );

        assertEquals( "tess", ((ImportantThing)list.get(1)).getName() );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }

    public void testComponentConfigurationWhereFieldsToConfigureResideInTheSuperclass()
        throws Exception
    {
        String xml =
            "<component>" +
            "  <name>jason</name>" +
            "  <address>bollywood</address>" +
            "</component>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ComponentConfigurator cc = new DefaultComponentConfigurator();

        cc.configureComponent( component, configuration );

        assertEquals( "jason", component.getName() );

        assertEquals( "bollywood", component.getAddress() );
    }
}
