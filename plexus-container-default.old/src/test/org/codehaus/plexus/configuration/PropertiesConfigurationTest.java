package org.codehaus.plexus.configuration;

import junit.framework.TestCase;

import java.util.Properties;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PropertiesConfigurationTest
    extends TestCase
{
    public void testPropertiesConfiguration()
        throws Exception
    {
        Properties properties = new Properties();

        properties.setProperty( "name", "jason" );

        properties.setProperty( "age", "31" );

        properties.setProperty( "crazy", "true" );

        properties.setProperty( "lazy", "false" );

        PropertiesConfiguration c = new PropertiesConfiguration( properties );

        assertEquals( 31, c.getChild( "age" ).getValueAsInteger() );

        assertEquals( 40, c.getChild( "time" ).getValueAsInteger( 40 ) );

        assertEquals( 31, c.getChild( "age" ).getValueAsLong() );

        assertEquals( 40, c.getChild( "time" ).getValueAsLong( 40 ) );

        assertEquals( new Float( 31 ), new Float( c.getChild( "age" ).getValueAsFloat() ) );

        assertEquals( new Float( 40 ), new Float( c.getChild( "time" ).getValueAsFloat( 40 ) ) );

        assertTrue( c.getChild( "crazy" ).getValueAsBoolean() );

        assertFalse( c.getChild( "lazy" ).getValueAsBoolean() );

        assertEquals( "jason", c.getChild( "name" ).getValue() );

        c.setProperty( "project", "plexus" );

        assertEquals( "plexus", c.getChild( "project" ).getValue() );

        try
        {
            c.getName();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getAttribute( "foo" );

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getAttributeNames();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getChildren();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getChildren( "foo" );

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getNamespace();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getPrefix();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getLocation();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }
    }

    public void testPublicConstructorPropertiesConfiguration()
        throws Exception
    {
        PropertiesConfiguration configuration = new PropertiesConfiguration();

        configuration.setProperty( "foo", "bar" );

        assertEquals( "bar", configuration.getChild( "foo" ).getValue() );

        try
        {
            configuration.getValue();
        }
        catch( UnsupportedOperationException e )
        {
            // do nothing
        }
    }

    public void testValueConfiguration()
        throws Exception
    {
        PropertiesConfiguration.ValueConfiguration c = new PropertiesConfiguration.ValueConfiguration( "foo", "bar" );

        assertEquals( "foo", c.getName() );

        assertEquals( "bar", c.getValue() );

        assertEquals( "unknown", c.getLocation() );

        try
        {
            c.getAttribute( "foo" );

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getAttributeNames();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getChildren();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getChildren( "foo" );

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getNamespace();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }

        try
        {
            c.getPrefix();

            fail( "UnsupportedOperationException should be thrown." );
        }
        catch ( UnsupportedOperationException e )
        {
        }
    }
}
