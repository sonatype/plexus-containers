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

        PropertiesPlexusConfiguration c = new PropertiesPlexusConfiguration( properties );

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
        PropertiesPlexusConfiguration configuration = new PropertiesPlexusConfiguration();

        configuration.setProperty( "foo", "bar" );

        assertEquals( "bar", configuration.getChild( "foo" ).getValue() );

        try
        {
            configuration.getValue();
        }
        catch ( UnsupportedOperationException e )
        {
            // do nothing
        }
    }

    public void testValueConfiguration()
        throws Exception
    {
        PropertiesPlexusConfiguration.ValueConfiguration c = new PropertiesPlexusConfiguration.ValueConfiguration( "foo", "bar" );

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
            c.getChild( "foo" );

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
