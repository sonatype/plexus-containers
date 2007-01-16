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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class CompositionUtilsTest
    extends PlexusTestCase
{

    /**
     * Test that {@link List} to array assignements throw a meaningful exception when they contain incompatible classes.
     * 
     * @throws Exception
     */
    public void testFindRequirementArray()
        throws Exception
    {
        Object component = "";

        Class clazz = ( new int[0] ).getClass();

        //Mock containerMock = mock( PlexusContainer.class );

        //PlexusContainer container = (PlexusContainer) containerMock.proxy();

        ComponentRequirement requirement = new ComponentRequirement();

        List dependencies = new ArrayList();

        dependencies.add( "" );

        //containerMock.expects( once() ).method( "lookupList" ).will( returnValue( dependencies ) );

        //containerMock.expects( once() ).method( "getComponentDescriptorList" ).will( returnValue( null ) );

        try
        {
            AbstractComponentComposer.findRequirement( component, clazz, container, requirement );

            fail( "should have thrown " + CompositionException.class.getName() );
        }
        catch ( CompositionException e )
        {
            // expected
        }
    }
}
