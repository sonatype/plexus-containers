package org.codehaus.plexus.component.repository;

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusTools;

import java.util.Set;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDescriptorTest
    extends TestCase
{
    public void testSimpleComponentResolution()
        throws Exception
    {
        String cc1 =
            "<component>" +
            "  <role>c1</role>" +
            "  <role-hint>role-hint</role-hint>" +
            "  <component-profile>component-profile</component-profile>" +
            "  <requirements>" +
            "    <requirement>c2</requirement>" +
            "    <requirement>c3</requirement>" +
            "  </requirements>" +
            "</component>";

        ComponentDescriptor c1 = PlexusTools.buildComponentDescriptor( cc1 );

        assertEquals( "c1", c1.getRole() );

        assertEquals( "role-hint", c1.getRoleHint() );

        assertEquals( "component-profile", c1.getComponentProfile() );

        Set requirements = c1.getRequirements();

        assertTrue( requirements.contains( "c2" ) );

        assertTrue( requirements.contains( "c3" ) );
    }
}
