package org.codehaus.plexus.component.composition;

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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;

import java.util.List;

/**
 *
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public abstract class AbstractCompositionResolverTest
    extends TestCase           
{

    /**
     *
     * @return
     */
    protected abstract CompositionResolver getCompositionResolver();


    // ------------------------------------------------------------------------
    //
    //     +-------+           +-------+
    //     |  c1   | --------> |  c2   |
    //     +-------+           +-------+
    //         |
    //         |
    //         V
    //     +-------+
    //     |  c3   |
    //     +-------+
    //
    // ------------------------------------------------------------------------
    public void testSimpleComponentResolution()
        throws Exception
    {
        String cc1 =
            "<component>" +
            "  <role>c1</role>" +
            "  <requirements>" +
            "    <requirement>" +
            "      <role>c2</role>" +
            "   </requirement>" +
            "    <requirement>" +
            "      <role>c3</role>" +
            "   </requirement>" +
            "  </requirements>" +
            "</component>";

        String cc2 =
            "<component>" +
            "  <role>c2</role>" +
            "</component>";

        String cc3 =
            "<component>" +
            "  <role>c3</role>" +
            "</component>";

        CompositionResolver compositionResolver = getCompositionResolver();

        ComponentDescriptor c1 = PlexusTools.buildComponentDescriptor( cc1 );

        ComponentDescriptor c2 = PlexusTools.buildComponentDescriptor( cc2 );

        ComponentDescriptor c3 = PlexusTools.buildComponentDescriptor( cc3 );

        compositionResolver.addComponentDescriptor( c1 );

        compositionResolver.addComponentDescriptor( c2 );

        compositionResolver.addComponentDescriptor( c3 );

        List dependencies = compositionResolver.getRequirements( c1.getComponentKey() );

        assertEquals( 2, dependencies.size() );

        assertTrue( dependencies.contains( c2.getRole() ) );

        assertTrue( dependencies.contains( c3.getRole() ) );

        assertEquals( 2, dependencies.size() );
    }

    // ------------------------------------------------------------------------
    //
    //     +-------+           +-------+
    //     |  c1   | --------> |  c2   |
    //     +-------+           +-------+
    //         |
    //         |
    //         V
    //     +-------+           +-------+
    //     |  c3   | --------> |  c4   |
    //     +-------+           +-------+
    //         |
    //         |
    //         V
    //     +-------+
    //     |  c5   |
    //     +-------+
    //
    // ------------------------------------------------------------------------
    public void testComplexComponentResolution()
        throws Exception
    {
        String cc1 =
            "<component>" +
            "  <role>c1</role>" +
            "  <requirements>" +
            "    <requirement>" +
            "      <role>c2</role>" +
            "   </requirement>" +
            "   <requirement>" +
            "      <role>c3</role>" +
            "   </requirement>" +
            "  </requirements>" +
            "</component>";

        String cc2 =
            "<component>" +
            "  <role>c2</role>" +
            "</component>";

        String cc3 =
            "<component>" +
            "  <role>c3</role>" +
            "  <requirements>" +
            "    <requirement>" +
            "      <role>c4</role>" +
            "   </requirement>" +
            "    <requirement>" +
            "      <role>c5</role>" +
            "   </requirement>" +
            "  </requirements>" +
            "</component>";

        String cc4 =
            "<component>" +
            "  <role>c4</role>" +
            "</component>";

        String cc5 =
            "<component>" +
            "  <role>c5</role>" +
            "</component>";


        CompositionResolver compositionResolver = getCompositionResolver();

        ComponentDescriptor c1 = PlexusTools.buildComponentDescriptor( cc1 );

        ComponentDescriptor c2 = PlexusTools.buildComponentDescriptor( cc2 );

        ComponentDescriptor c3 = PlexusTools.buildComponentDescriptor( cc3 );

        ComponentDescriptor c4 = PlexusTools.buildComponentDescriptor( cc4 );

        ComponentDescriptor c5 = PlexusTools.buildComponentDescriptor( cc5 );

        compositionResolver.addComponentDescriptor( c1 );

        compositionResolver.addComponentDescriptor( c2 );

        compositionResolver.addComponentDescriptor( c3 );

        compositionResolver.addComponentDescriptor( c4 );

        compositionResolver.addComponentDescriptor( c5 );

        List dependencies = compositionResolver.getRequirements( c1.getComponentKey() );

        assertEquals( 2, dependencies.size() );

        // I just leave this at the moment as I am just 99% sure that this is not needed and not
        // correct. compositionResolver.getComponentDependencies() should return only direct dependencies
        //
        // I will need to add a method like getSortedComponents()
        // which will do topological sort of DAG and return list of ordered component which can be used
        // by ComponentComposer.
        // Possibility of checking if there are cycles probably also must be exposed in API (DAG has it alredy)
        // and it should be used
        // I can implement cycle detecting from single node (source) as after adding new component
        // we don't have to probably check entire graph but we will probably have to check
        // if there are cycles.

        /**
        // c5 must come before c3
        assertTrue( dependencies.indexOf( "c5" ) < dependencies.indexOf( "c3" ) );

        // c4 must come before c3
        assertTrue( dependencies.indexOf( "c4" ) < dependencies.indexOf( "c3" ) );
        */
    }
}
