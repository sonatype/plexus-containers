package org.codehaus.plexus.configuration;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;

import java.io.StringReader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class CascadingConfigurationTest
    extends TestCase
{
    private XmlPullConfigurationBuilder cb;

    private String baseXml;

    private String parentXml;

    private Configuration base;

    private Configuration parent;

    public CascadingConfigurationTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        cb = new XmlPullConfigurationBuilder();

        baseXml = "<conf>" +
                    "<type default='foo'>jason</type>" +
                    "<name>jason</name>" +
                    "<number>0</number>" +
                    "<boolean>true</boolean>" +
                  "</conf>";

        parentXml = "<conf>" +
                      "<type default='bar'>jason</type>" +
                      "<occupation>procrastinator</occupation>" +
                      "<foo a1='1' a2='2' number='0'>bar</foo>" +
                    "</conf>";

        base = cb.parse( new StringReader( baseXml ) );

        parent = cb.parse( new StringReader( parentXml ) );
    }

    public void testSimpleConfigurationCascading()
        throws Exception
    {
        CascadingConfiguration cc = new CascadingConfiguration( base, parent );

        // Take a value from the base.
        assertEquals( "jason", cc.getChild( "name" ).getValue() );

        // Take a value from the parent.
        assertEquals( "procrastinator", cc.getChild( "occupation" ).getValue() );

        // We want the 'default' attribute from the base, which effectively overrides
        // the 'default' attribute in the parent configuration.
        assertEquals( "foo", cc.getChild( "type" ).getAttribute( "default" ) );

        assertEquals( 0, cc.getChild( "number" ).getValueAsInteger() );

        assertEquals( 0, cc.getChild( "number" ).getValueAsLong() );

        assertEquals( new Float( 0 ), new Float( cc.getChild( "number" ).getValueAsFloat() ) );

        assertTrue( cc.getChild( "boolean" ).getValueAsBoolean() );

        assertTrue( cc.getChild( "non-existent-boolean" ).getValueAsBoolean( true ) );

        assertNotNull( cc.getChild( "foo" ).getAttributeNames() );

        assertEquals( 3, cc.getChild( "foo" ).getAttributeNames().length );

        // Create a new configuration.
        Configuration c = cc.getChild( "new", true );

        assertNotNull( c );

        assertEquals( 0, cc.getChild( "foo" ).getAttributeAsInteger( "number" ) );

        assertEquals( 0, cc.getChild( "foo" ).getAttributeAsLong( "number" ) );

        assertEquals( new Float( 0 ), new Float( cc.getChild( "foo" ).getAttributeAsFloat( "number" ) ) );
    }

    public void testCascadingConfigurationWithNullParent()
        throws Exception
    {
        CascadingConfiguration cc = new CascadingConfiguration( base, null );

        // Take a value from the base.
        assertEquals( "jason", cc.getChild( "name" ).getValue() );

        assertEquals( "conf", cc.getName() );

        assertNotNull( cc.getLocation() );

        assertNotNull( cc.getNamespace() );
    }

    public void testCascadingConfigurationWithNullBase()
        throws Exception
    {
        CascadingConfiguration cc = new CascadingConfiguration( null, parent );

        // Take a value from the parent.
        assertEquals( "procrastinator", cc.getChild( "occupation" ).getValue() );

        assertEquals( "-", cc.getName() );
    }
}
