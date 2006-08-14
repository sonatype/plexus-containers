package org.codehaus.plexus.component.composition;

/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Test for {@link CompositionUtils}
 * 
 * @author <a href="mailto:carlos@apache.org">Carlos Sanchez</a>
 * @version $Id$
 */
public class CompositionUtilsTest
    extends MockObjectTestCase
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
        Mock containerMock = mock( PlexusContainer.class );
        PlexusContainer container = (PlexusContainer) containerMock.proxy();
        ComponentRequirement requirement = new ComponentRequirement();

        List dependencies = new ArrayList();
        dependencies.add( "" );
        containerMock.expects( once() ).method( "lookupList" ).will( returnValue( dependencies ) );
        containerMock.expects( once() ).method( "getComponentDescriptorList" ).will( returnValue( null ) );

        try
        {
            CompositionUtils.findRequirement( component, clazz, container, requirement );
            fail( "should have thrown " + CompositionException.class.getName() );
        }
        catch ( CompositionException e )
        {
            // expected
        }
    }
}
