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
    public CascadingConfigurationTest( String s )
    {
        super( s );
    }

    public void testSimpleConfigurationCascading()
        throws Exception
    {
        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();

        String s0 = "<conf>" +
                      "<type default='foo'>jason</type>" +
                      "<name>jason</name>" +
                    "</conf>";

        String s1 = "<conf>" +
                      "<type default='bar'>jason</type>" +
                      "<occupation>procrastinator</occupation>" +
                    "</conf>";

        Configuration base = cb.parse( new StringReader( s0 ) );

        Configuration parent = cb.parse( new StringReader( s1 ) );

        CascadingConfiguration cc = new CascadingConfiguration( base, parent );

        // Take a value from the base.
        assertEquals( "jason", cc.getChild( "name" ).getValue() );

        // Take a value from the parent.
        assertEquals( "procrastinator", cc.getChild( "occupation" ).getValue() );

        // We want the 'default' attribute from the base, which effectively overrides
        // the 'default' attribute in the parent configuration.
        assertEquals( "foo", cc.getChild( "type" ).getAttribute( "default" ) );
    }
}
