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

        String s0 = "<conf><name>jason</name></conf>";
        Configuration base = cb.parse( new StringReader( s0 ) );

        String s1 = "<conf><occupation>procrastinator</occupation></conf>";
        cb = new XmlPullConfigurationBuilder();
        Configuration parent = cb.parse( new StringReader( s1 ) );

        CascadingConfiguration cc = new CascadingConfiguration( base, parent );

        // Take a value from the base.
        assertEquals( "jason", cc.getChild( "name" ).getValue() );

        // Take a value from the parent.
        assertEquals( "procrastinator", cc.getChild( "occupation" ).getValue() );
    }
}
