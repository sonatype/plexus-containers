package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;

import java.io.StringReader;
import java.util.Set;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDescriptorBuilderTest
    extends TestCase
{
    private static String configuration =
        "<component>" +
          "<role>role</role>" +
          "<role-hint>role-hint</role-hint>" +
          "<implementation>implementation</implementation>" +
          "<instantiation-strategy>instantiation-strategy</instantiation-strategy>" +
          "<lifecycle-handler>lifecycle-handler</lifecycle-handler>" +
          "<requirements>" +
            "<requirement>requirement</requirement>" +
          "</requirements>" +
          "<configuration>" +
            "<name>name</name>" +
          "</configuration>" +
        "</component>";

    public ComponentDescriptorBuilderTest( String s )
    {
        super( s );
    }

    public void testComponentDescriptorBuilder()
        throws Exception
    {
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        Configuration c = builder.parse( new StringReader( configuration ) );

        ComponentDescriptorBuilder cdb = new ComponentDescriptorBuilder();

        ComponentDescriptor cd = cdb.build( c );

        assertEquals( "role", cd.getRole() );

        assertEquals( "role-hint", cd.getRoleHint() );

        assertEquals( "implementation", cd.getImplementation() );

        assertEquals( "instantiation-strategy", cd.getInstantiationStrategy() );

        assertEquals( "lifecycle-handler", cd.getLifecycleHandler() );

        Set requirements = cd.getRequirements();

        assertNotNull( requirements );

        assertTrue( requirements.contains( "requirement" ) );

        Configuration cc = cd.getConfiguration();

        assertNotNull( cc );

        assertEquals( "name", cc.getChild( "name" ).getValue() );
    }
}
