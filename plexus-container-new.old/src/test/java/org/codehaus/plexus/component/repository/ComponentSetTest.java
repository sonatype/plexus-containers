package org.codehaus.plexus.component.repository;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.component.repository.io.PlexusTools;

import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentSetTest
    extends TestCase
{
    public void testSimpleComponentResolution()
        throws Exception
    {
        String xml =
            "<component-set>" +
            "  <components>" +
            "    <component>" +
            "      <role>c1</role>" +
            "      <role-hint>role-hint</role-hint>" +
            "      <component-profile>component-profile</component-profile>" +
            "      <requirements>" +
            "        <requirement>" +
            "          <role>c2</role>" +
            "        </requirement>" +
            "        <requirement>" +
            "          <role>c3</role>" +
            "        </requirement>" +
            "      </requirements>" +
            "    </component>" +
            "  </components>" +
            "  <dependencies>" +
            "    <dependency>" +
            "      <group-id>plexus</group-id>" +
            "      <artifact-id>wedgy</artifact-id>" +
            "      <version>1.0</version>" +
            "    </dependency>" +
            "  </dependencies>" +
            "</component-set>";

        ComponentSetDescriptor cs = PlexusTools.buildComponentSet( PlexusTools.buildConfiguration( xml ) );

        ComponentDescriptor c1 = (ComponentDescriptor) cs.getComponents().get( 0 );

        assertEquals( "c1", c1.getRole() );

        assertEquals( "role-hint", c1.getRoleHint() );

        assertEquals( "component-profile", c1.getComponentProfile() );

        List requirements = c1.getRequirements();

        assertEquals( 2, requirements.size() );

        boolean containsC2 = false;

        boolean containsC3 = false;

        for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) iterator.next();

            if ( requirement.getRole().equals( "c2" ) )
            {
                containsC2 = true;
            }
            else if ( requirement.getRole().equals( "c3" ) )
            {
                containsC3 = true;
            }

        }

        assertTrue( containsC2 );

        assertTrue( containsC3 );

        ComponentDependency d1 = (ComponentDependency) cs.getDependencies().get( 0 );

        assertEquals( "plexus", d1.getGroupId() );

        assertEquals( "wedgy", d1.getArtifactId() );

        assertEquals( "1.0", d1.getVersion() );
    }
}
