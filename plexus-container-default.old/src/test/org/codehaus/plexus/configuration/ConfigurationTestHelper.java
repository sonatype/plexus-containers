package org.codehaus.plexus.configuration;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;

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
    public static void testConfiguration( Configuration c )
        throws Exception
    {
        // Exercise all value/attribute retrieval methods.

        // Integer

        assertEquals( 0, c.getChild( "number" ).getValueAsInteger() );

        assertEquals( 1, c.getChild( "ne-number" ).getValueAsInteger( 1 ) );

        // Long

        assertEquals( 0, c.getChild( "number" ).getValueAsLong() );

        assertEquals( 1, c.getChild( "ne-number" ).getValueAsLong( 1 ) );

        // Float

        assertEquals( new Float( 0 ), new Float( c.getChild( "number" ).getValueAsFloat() ) );

        assertEquals( new Float( 1 ), new Float( c.getChild( "ne-number" ).getValueAsFloat( 1 ) ) );

        // Boolean

        assertTrue( c.getChild( "boolean-true" ).getValueAsBoolean() );

        assertTrue( c.getChild( "ne-boolean-true" ).getValueAsBoolean( true ) );

        assertFalse( c.getChild( "boolean-false" ).getValueAsBoolean() );

        assertFalse( c.getChild( "ne-boolean-false" ).getValueAsBoolean( false ) );

    }
}
