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

    public static PlexusConfiguration getTestConfiguration()
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

        // Integer

        assertEquals( 0, c.getChild( "number" ).getValueAsInteger() );

        assertEquals( 1, c.getChild( "ne-number" ).getValueAsInteger( 1 ) );

        try
        {
            c.getChild( "not-a-number" ).getValueAsInteger();

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }

        // Long

        assertEquals( 0, c.getChild( "number" ).getValueAsLong() );

        assertEquals( 1, c.getChild( "ne-number" ).getValueAsLong( 1 ) );

        try
        {
            c.getChild( "not-a-number" ).getValueAsLong();

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }

        // Float

        assertEquals( new Float( 0 ), new Float( c.getChild( "number" ).getValueAsFloat() ) );

        assertEquals( new Float( 1 ), new Float( c.getChild( "ne-number" ).getValueAsFloat( 1 ) ) );

        try
        {
            c.getChild( "not-a-number" ).getValueAsFloat();

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }

        // Boolean

        assertTrue( c.getChild( "boolean-true" ).getValueAsBoolean() );

        assertTrue( c.getChild( "ne-boolean-true" ).getValueAsBoolean( true ) );

        assertFalse( c.getChild( "boolean-false" ).getValueAsBoolean() );

        assertFalse( c.getChild( "ne-boolean-false" ).getValueAsBoolean( false ) );

        try
        {
            c.getChild( "not-a-boolean" ).getValueAsBoolean();

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {

        }

        // Attributes

        // Integer

        assertEquals( 0, c.getChild( "number" ).getAttributeAsInteger( "number" ) );

        assertEquals( 1, c.getChild( "number" ).getAttributeAsInteger( "ne-number", 1 ) );

        try
        {
            c.getChild( "not-a-number" ).getAttributeAsInteger( "not-a-number" );

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }


        // Long

        assertEquals( 0, c.getChild( "number" ).getAttributeAsLong( "number" ) );

        assertEquals( 1, c.getChild( "number" ).getAttributeAsLong( "ne-number", 1 ) );

        try
        {
            c.getChild( "not-a-number" ).getAttributeAsLong( "not-a-number" );

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }


        // Float

        assertEquals( new Float( 0 ), new Float( c.getChild( "number" ).getAttributeAsFloat( "number" ) ) );

        assertEquals( new Float( 1 ), new Float( c.getChild( "number" ).getAttributeAsFloat( "ne-number", 1 ) ) );

        try
        {
            c.getChild( "not-a-number" ).getAttributeAsFloat( "not-a-number" );

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }

        // Boolean

        assertTrue( c.getChild( "boolean-true" ).getAttributeAsBoolean( "boolean-true" ) );

        assertTrue( c.getChild( "boolean-true" ).getAttributeAsBoolean( "ne-boolean-true", true ) );

        assertFalse( c.getChild( "boolean-false" ).getAttributeAsBoolean( "boolean-false" ) );

        assertFalse( c.getChild( "boolean-false" ).getAttributeAsBoolean( "boolean-false", false ) );

        try
        {
            c.getChild( "string" ).getAttributeAsBoolean( "string" );

            fail( "A ConfigurationException should be thrown." );
        }
        catch ( ConfigurationException e )
        {
            // do nothing
        }

    }
}
