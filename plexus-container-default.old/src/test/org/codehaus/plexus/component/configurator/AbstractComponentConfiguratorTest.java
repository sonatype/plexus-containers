package org.codehaus.plexus.component.configurator;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;

import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfiguratorTest extends TestCase
{
    public AbstractComponentConfiguratorTest( String s )
    {
        super( s );
    }

    protected abstract ComponentConfigurator getComponentConfigurator();

    public void testComponentConfigurator()
            throws Exception
    {
        String xml =
                "<configuration>" +
                "  <int-value>0</int-value>" +
                "  <float-value>1</float-value>" +
                "  <long-value>2</long-value>" +
                "  <double-value>3</double-value>" +
                "  <string-value>foo</string-value>" +
                "  <important-things>" +
                "    <important-thing><name>jason</name></important-thing>" +
                "    <important-thing><name>tess</name></important-thing>" +
                "  </important-things>" +
                "  <configuration>" +
                "      <name>jason</name>" +
                "  </configuration>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        ConfigurableComponent component = new ConfigurableComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        cc.configureComponent( component, descriptor, configuration );

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertEquals( "check long value", 2L, component.getLongValue() );

        Assert.assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertEquals( "foo", component.getStringValue() );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( ( ImportantThing ) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( ( ImportantThing ) list.get( 1 ) ).getName() );


        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }


    public void testComponentConfigurationWhereFieldsToConfigureResideInTheSuperclass()
            throws Exception
    {
        String xml =
                "<configuration>" +
                "  <name>jason</name>" +
                "  <address>bollywood</address>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        cc.configureComponent( component, descriptor, configuration );

        assertEquals( "jason", component.getName() );

        assertEquals( "bollywood", component.getAddress() );
    }


    /**
     * @todo THIS Tests work for new component configurator but does not work
     * for old component configurator
     * @throws Exception
     */
    public void _testComponentConfigurationWhereFieldsAreCollections()
            throws Exception
    {
        String xml =
                "<configuration>" +
                "  <vector>" +
                "    <important-thing>" +
                "       <name>life</name>" +
                "    </important-thing>" +
                "  </vector>" +
                "  <set>" +
                "    <important-thing>" +
                "       <name>life</name>" +
                "    </important-thing>" +
                "  </set>" +
                "   <list implementation=\"java.util.LinkedList\">" +
                "     <important-thing>" +
                "       <name>life</name>" +
                "    </important-thing>" +
                "  </list>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        ComponentWithCollectionFields component = new ComponentWithCollectionFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        cc.configureComponent( component, descriptor, configuration );

        List list = component.getList();

        assertNotNull( list );

        System.out.println( "list: " + list );

        assertEquals( list.getClass(), LinkedList.class );

        assertEquals( 1, list.size() );

    }

    public void testComponentConfigurationWithCompositeFields()
            throws Exception
    {

        String xml =
                "<configuration>" +
                "  <thing implementation=\"org.codehaus.plexus.component.configurator.ImportantThing\">" +
                "     <name>I am not abstract!</name>" +
                "  </thing>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        ComponentWithCompositeFields component = new ComponentWithCompositeFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        cc.configureComponent( component, descriptor, configuration );

        assertNotNull( component.getThing() );

        assertEquals( "I am not abstract!", component.getThing().getName() );

    }

    public void testComponentConfigurationWithPropertiesFields()
            throws Exception
    {

        String xml =
                "<configuration>" +
                "  <someProperties>" +
                "     <property>" +
                "        <name>firstname</name>" +
                "        <value>michal</value>" +
                "     </property>" +
                "     <property>" +
                "        <name>lastname</name>" +
                "        <value>maczka</value>" +
                "     </property>" +
                "  </someProperties>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( new StringReader( xml ) );

        ComponentWithPropertiesField component = new ComponentWithPropertiesField();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        cc.configureComponent( component, descriptor, configuration );

        Properties properties = component.getSomeProperties();

        assertNotNull( properties  );

        assertEquals( "michal" , properties.get( "firstname"  ) );

        assertEquals( "maczka" , properties.get( "lastname"  ) );

    }

}
