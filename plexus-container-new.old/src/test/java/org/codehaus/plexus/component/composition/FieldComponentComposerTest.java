package org.codehaus.plexus.component.composition;

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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class FieldComponentComposerTest
    extends TestCase
{
    public void testGetFieldByName()
    {
        ComponentF componentF = new ComponentF();

        final FieldComponentComposer composer = new FieldComponentComposer();

        try
        {
            Field fieldA = composer.getFieldByName( componentF, "componentA", null );

            assertEquals( ComponentA.class, fieldA.getType() );

            Field fieldB = composer.getFieldByName( componentF, "componentB", null );

            assertEquals( ComponentB.class, fieldB.getType() );

            // we have arrays of C components
            Field fieldC = composer.getFieldByName( componentF, "componentC", null );

            assertTrue( fieldC.getType().isArray() );
        }
        catch ( CompositionException e )
        {
            fail( e.getMessage() );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( "myRole" );

        componentDescriptor.setRoleHint( "myRoleHint" );

        try
        {
            composer.getFieldByName( componentF, "dummy", componentDescriptor );

            fail( "Exception was expected" );
        }
        catch ( CompositionException e )
        {
            assertTrue( e.getMessage().indexOf( "myRole" ) > 0 );

            assertTrue( e.getMessage().indexOf( "myRoleHint" ) > 0 );
        }
    }


    public void testGetFieldByType()
    {
        ComponentF componentF = new ComponentF();

        final FieldComponentComposer composer = new FieldComponentComposer();

        try
        {
            Field fieldA = composer.getFieldByType( componentF, ComponentA.class, null );

            assertEquals( ComponentA.class, fieldA.getType() );

            assertFalse( fieldA.getType().isArray() );

            Field fieldB = composer.getFieldByType( componentF, ComponentB.class, null );

            assertEquals( ComponentB.class, fieldB.getType() );

            assertFalse( fieldB.getType().isArray() );

            Field fieldC = composer.getFieldByType( componentF, ComponentC.class, null );

            assertTrue( fieldC.getType().isArray() );
        }
        catch ( CompositionException e )
        {
            fail( e.getMessage() );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( "myRole" );

        componentDescriptor.setRoleHint( "myRoleHint" );

        try
        {
            composer.getFieldByType( componentF, ComponentF.class, componentDescriptor );

            fail( "Exception was expected" );
        }
        catch ( CompositionException e )
        {
            assertTrue( e.getMessage().indexOf( "myRole" ) > 0 );

            assertTrue( e.getMessage().indexOf( "myRoleHint" ) > 0 );
        }
    }

    public void testFindMatchingField()
    {
        ComponentF componentF = new ComponentF();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( ComponentF.class.getName() );

        componentDescriptor.setRoleHint( "myRoleHint" );

        ComponentRequirement requirementA = new ComponentRequirement();

        requirementA.setRole( ComponentA.class.getName() );

        componentDescriptor.addRequirement( requirementA );

        ComponentRequirement requirementD = new ComponentRequirement();

        requirementD.setRole( ComponentD.class.getName() );

        requirementD.setFieldName( "componentD" );

        componentDescriptor.addRequirement( requirementD );

        final FieldComponentComposer composer = new FieldComponentComposer();

        try
        {
            composer.findMatchingField( componentF, componentDescriptor, requirementA, null );

            composer.findMatchingField( componentF, componentDescriptor, requirementD, null );
        }
        catch ( CompositionException e )
        {
            fail( e.getMessage() );
        }
    }
}
