package org.codehaus.plexus.configuration.xml.xstream.converters;

import java.util.Properties;

import junit.framework.TestCase;

import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;

public class PropertiesConverterTest
    extends TestCase
{
    public void testConvertsPropertiesObjectToShortKeyValueElements()
    {
        Properties in = new Properties();

        in.setProperty( "hello", "world" );

        in.setProperty( "foo", "cheese" );

        PlexusXStream xStream = new PlexusXStream();

        String expectedXML = "" +
            "<properties>\n" +
            "  <property>\n" +
            "    <name>hello</name>\n" +
            "    <value>world</value>\n" +
            "  </property>\n" +
            "  <property>\n" +
            "    <name>foo</name>\n" +
            "    <value>cheese</value>\n" +
            "  </property>\n" +
            "</properties>";

        String actualXML = xStream.toXML( in );

        assertEquals( expectedXML, actualXML );

        Properties expectedOut = new Properties();

        expectedOut.setProperty( "hello", "world" );

        expectedOut.setProperty( "foo", "cheese" );

        Properties actualOut = (Properties) xStream.fromXML( actualXML );

        assertEquals( in, actualOut );

        assertEquals( in.toString(), actualOut.toString() );
    }
}
