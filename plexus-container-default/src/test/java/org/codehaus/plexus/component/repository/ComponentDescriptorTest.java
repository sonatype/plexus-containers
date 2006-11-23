package org.codehaus.plexus.component.repository;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
            "    <requirement>" +
            "      <role>c2</role>" +
            "   </requirement>" +
            "    <requirement>" +
            "      <role>c3</role>" +
            "   </requirement>" +
            "  </requirements>" +
            "</component>";

        ComponentDescriptor c1 = PlexusTools.buildComponentDescriptor( cc1 );

        assertEquals( "c1", c1.getRole() );

        assertEquals( "role-hint", c1.getRoleHint() );

        assertEquals( "component-profile", c1.getComponentProfile() );

        List requirements = c1.getRequirements();

        assertEquals( 2, requirements.size() );

        boolean containsC2 = false;

        boolean containsC3 = false;

        for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            ComponentRequirement requirement = ( ComponentRequirement ) iterator.next();

            if ( requirement.getRole().equals( "c2" ))
            {
                containsC2 = true;
            }
            else if ( requirement.getRole().equals( "c3" ))
            {
                containsC3 = true;
            }

        }

        assertTrue( containsC2 );

        assertTrue( containsC3 );
    }
    
    public void testShouldNotBeEqualWhenRolesAreSameButHintsAreDifferent()
    {
        ComponentDescriptor desc = new ComponentDescriptor();
        desc.setRole("one");
        desc.setRoleHint("one");
        
        ComponentDescriptor desc2 = new ComponentDescriptor();
        desc2.setRole("one");
        desc2.setRoleHint("two");
        
        assertFalse(desc.equals(desc2));
    }
}
