package org.codehaus.plexus.component.repository.io;

import junit.framework.TestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.StringReader;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentDescriptorReaderTest
    extends TestCase
{
    private String componentDescriptor =
        "<component>" +
        "  <role>role</role>" +
        "  <role-hint>role-hint</role-hint>" +
        "  <implementation>implementation</implementation>" +
        "  <version>version</version>" +
        "  <description>description</description>" +
        "  <component-type>component-type</component-type>" +
        "  <instantiation-strategy>instantiation-strategy</instantiation-strategy>" +
        "  <lifecycle-handler>lifecycle-handler</lifecycle-handler>" +
        "  <component-profile>component-profile</component-profile>" +
        "  <component-composer>component-composer</component-composer>" +
        "  <component-factory>component-factory</component-factory>" +
        "  <alias>alias</alias>" +
        "  <configuration>" +
        "    <name>name</name>" +
        "    <listener>" +
        "      <host>localhost</host>" +
        "      <port>8080</port>" +
        "    </listener>" +
        "  </configuration>" +
        "  <requirements>" +
        "    <requirement>" +
        "      <role>requirement-role0</role>" +
        "    </requirement>" +
        "    <requirement>" +
        "      <role>requirement-role1</role>" +
        "      <field-name>field-name</field-name>" +
        "    </requirement>" +
        "  </requirements>" +
        "</component>";

    private String componentSetDescriptor =
        "<component-set>" +
        "  <components>" +
             componentDescriptor +
        "  </components>" +
        "  <dependencies>" +
        "    <dependency>" +
        "      <group-id>group-id</group-id>" +
        "      <artifact-id>artifact-id</artifact-id>" +
        "      <version>version</version>" +
        "    </dependency>" +
        "  </dependencies>" +
        "  <isolated-realm>true</isolated-realm>" +
        "</component-set>";

    public void testComponentDescriptorReader()
        throws Exception
    {
        ComponentDescriptorReader cdr = new ComponentDescriptorReader();

        ComponentDescriptor cd = cdr.parseComponentDescriptor( new StringReader( componentDescriptor ) );

        validateComponentDescriptor( cd );
    }

    public void testComponentSetDescriptorReader()
        throws Exception
    {
        ComponentDescriptorReader cdr = new ComponentDescriptorReader();

        ComponentSetDescriptor csd = cdr.parseComponentSetDescriptor( new StringReader( componentSetDescriptor ) );

        validateComponentDescriptor( (ComponentDescriptor) csd.getComponents().get( 0 ) );

        // ----------------------------------------------------------------------
        // Component dependencies
        // ----------------------------------------------------------------------

        List dependencies = csd.getDependencies();

        ComponentDependency cd0 = (ComponentDependency) dependencies.get( 0 );

        assertEquals( "group-id", cd0.getGroupId() );

        assertEquals( "artifact-id", cd0.getArtifactId() );

        assertEquals( "version", cd0.getVersion() );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        assertTrue( csd.isIsolatedRealm() );
    }

    public void validateComponentDescriptor( ComponentDescriptor cd )
        throws Exception
    {
        assertEquals( "role", cd.getRole() );

        assertEquals( "role-hint", cd.getRoleHint() );

        assertEquals( "implementation", cd.getImplementation() );

        assertEquals( "version", cd.getVersion() );

        assertEquals( "description", cd.getDescription() );

        assertEquals( "component-type", cd.getComponentType() );

        assertEquals( "instantiation-strategy", cd.getInstantiationStrategy() );

        assertEquals( "lifecycle-handler", cd.getLifecycleHandler() );

        assertEquals( "component-profile", cd.getComponentProfile() );

        assertEquals( "component-composer", cd.getComponentComposer() );

        assertEquals( "component-factory", cd.getComponentFactory() );

        assertEquals( "alias", cd.getAlias() );

        // ----------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------

        PlexusConfiguration c = cd.getConfiguration();

        assertEquals( "name", c.getChild( "name" ).getValue() );

        PlexusConfiguration listener = c.getChild( "listener" );

        assertNotNull( listener );

        assertEquals( "localhost", listener.getChild( "host" ).getValue() );

        assertEquals( "8080", listener.getChild( "port" ).getValue() );

        // ----------------------------------------------------------------------
        // Requirements
        // ----------------------------------------------------------------------

        List requirements = cd.getRequirements();

        ComponentRequirement cr0 = (ComponentRequirement) requirements.get( 0 );

        assertEquals( "requirement-role0", cr0.getRole() );

        ComponentRequirement cr1 = (ComponentRequirement) requirements.get( 1 );

        assertEquals( "requirement-role1", cr1.getRole() );

        assertEquals( "field-name", cr1.getFieldName() );
    }
}
