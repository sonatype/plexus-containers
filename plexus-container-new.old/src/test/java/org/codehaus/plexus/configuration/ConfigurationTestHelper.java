package org.codehaus.plexus.configuration;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;

import java.io.StringReader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ConfigurationTestHelper
    extends TestCase
{
    /** Configuration builder. */
    private static XmlPullConfigurationBuilder configurationBuilder = new XmlPullConfigurationBuilder();

    public static Configuration getTestConfiguration()
        throws Exception
    {
        return configurationBuilder.parse( new StringReader( ConfigurationTestHelper.getXmlConfiguration() ) );
    }

    public static String getXmlConfiguration()
    {
        return "<configuration>" +
            "<string string='string'>string</string>" +
            "<number number='0'>0</number>" +
            "<not-a-number not-a-number='foo'>not-a-number</not-a-number>" +
            "<boolean-true boolean-true='true'>true</boolean-true>" +
            "<boolean-false boolean-false='false'>false</boolean-false>" +
            "<not-a-boolean>not-a-boolean</not-a-boolean>" +
            "</configuration>";
    }

    public static void testConfiguration( Configuration c )
        throws Exception
    {
        // Exercise all value/attribute retrieval methods.

        // Values

        // String

        assertEquals( "string", c.getValue( "string" ) );

        assertEquals( "string", c.getChild( "string" ).getValue() );

        assertEquals( "string", c.getChild( "ne-string" ).getValue( "string" ) );
    }
}
