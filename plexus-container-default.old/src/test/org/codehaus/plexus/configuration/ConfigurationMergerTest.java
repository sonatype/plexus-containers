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
public class ConfigurationMergerTest
    extends TestCase
{
    /** Configuration builder. */
    private XmlPullConfigurationBuilder configurationBuilder;

    /** Base xml string for base configuration. */
    private String baseXml;

    /** Parent xml string for layer configuration. */
    private String layerXml;

    /**  Base configuration. */
    private Configuration base;

    /** Parent Configuration. */
    private Configuration layer;

    public ConfigurationMergerTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        configurationBuilder = new XmlPullConfigurationBuilder();

        baseXml =
            "<conf>" +
            "  <type default='bar'>jason</type>" +
            "  <name>jason</name>" +
            "  <number>0</number>" +
            "  <boolean>true</boolean>" +
            "  <logging>" +
            "    <implementation>base</implementation>" +
            "  </logging>" +
            "</conf>";

        layerXml =
            "<conf>" +
            "  <type default='foo'>jason</type>" +
            "  <occupation>procrastinator</occupation>" +
            "  <foo a1='1' a2='2' number='0'>bar</foo>" +
            "  <logging>" +
            "    <implementation>layer</implementation>" +
            "  </logging>" +
            "</conf>";

        base = configurationBuilder.parse( new StringReader( baseXml ) );

        layer = configurationBuilder.parse( new StringReader( layerXml ) );
    }

    public void testWithHelper()
        throws Exception
    {
        Configuration c = ConfigurationTestHelper.getTestConfiguration();

        Configuration cc = ConfigurationMerger.merge( new DefaultConfiguration( "" ), c );

        ConfigurationTestHelper.testConfiguration( cc );
    }

    public void testSimpleConfigurationCascading()
        throws Exception
    {
        Configuration cc = ConfigurationMerger.merge( layer, base );

        // Take a value from the base.
        assertEquals( "jason", cc.getChild( "name" ).getValue() );

        // Take a value from the layer.
        assertEquals( "procrastinator", cc.getChild( "occupation" ).getValue() );

        // We want the 'default' attribute from the layer, which effectively overrides
        // the 'default' attribute in the base configuration.
        assertEquals( "foo", cc.getChild( "type" ).getAttribute( "default" ) );

        assertEquals( "layer", cc.getChild( "logging" ).getChild( "implementation" ).getValue() );

        assertNotNull( cc.getChild( "foo" ).getAttributeNames() );

        assertEquals( 3, cc.getChild( "foo" ).getAttributeNames().length );

        // Create a new configuration.
        Configuration c = cc.getChild( "new", true );

        assertNotNull( c );
    }
}
