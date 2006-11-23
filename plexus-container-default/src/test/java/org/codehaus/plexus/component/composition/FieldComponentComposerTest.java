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
        throws Exception
    {
        ComponentF componentF = new ComponentF();

        final FieldComponentComposer composer = new FieldComponentComposer();

        Field fieldA = composer.getFieldByName( componentF, "componentA", null );

        assertEquals( ComponentA.class, fieldA.getType() );

        Field fieldB = composer.getFieldByName( componentF, "componentB", null );

        assertEquals( ComponentB.class, fieldB.getType() );

        // we have arrays of C components
        Field fieldC = composer.getFieldByName( componentF, "componentC", null );

        assertTrue( fieldC.getType().isArray() );

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
        throws Exception
    {
        ComponentF componentF = new ComponentF();

        final FieldComponentComposer composer = new FieldComponentComposer();

        Field fieldA = composer.getFieldByType( componentF, ComponentA.class, null );

        assertEquals( ComponentA.class, fieldA.getType() );

        assertFalse( fieldA.getType().isArray() );

        Field fieldB = composer.getFieldByType( componentF, ComponentB.class, null );

        assertEquals( ComponentB.class, fieldB.getType() );

        assertFalse( fieldB.getType().isArray() );

        Field fieldC = composer.getFieldByType( componentF, ComponentC.class, null );

        assertTrue( fieldC.getType().isArray() );

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
        throws Exception
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

        composer.findMatchingField( componentF, componentDescriptor, requirementA, null );

        composer.findMatchingField( componentF, componentDescriptor, requirementD, null );
    }

    public void testCompositionOfComponentsWithSeveralFieldsOfTheSameType()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Set up
        // ----------------------------------------------------------------------

        // Add a single requirement
        ComponentRequirement requirementOne = new ComponentRequirement();

        requirementOne.setRole( ComponentE.class.getName() );

        // The descriptor
        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "1" );

        descriptor.setImplementation( ComponentWithSeveralFieldsOfTheSameType.class.getName() );

        descriptor.addRequirement( requirementOne );

        // ----------------------------------------------------------------------
        // Assert that this fails as there is one requirement and
        // two fields that will match the requirement
        // ----------------------------------------------------------------------

        FieldComponentComposer composer = new FieldComponentComposer();

        ComponentWithSeveralFieldsOfTheSameType component = new ComponentWithSeveralFieldsOfTheSameType();

        try
        {
            composer.findMatchingField( component, descriptor, requirementOne, null );

            fail( "Expected CompositionException" );
        }
        catch( CompositionException ex )
        {
            assertTrue( ex.getMessage().startsWith( "There are several fields of type" ) );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        // Make a second requirement without a field name
        ComponentRequirement requirementTwo = new ComponentRequirement();

        requirementTwo.setRole( ComponentE.class.getName() );

        descriptor.addRequirement( requirementTwo );

        try
        {
            composer.findMatchingField( component, descriptor, requirementOne, null );

            fail( "Expected CompositionException" );
        }
        catch ( CompositionException ex )
        {
            assertTrue( ex.getMessage().startsWith( "There are several fields of type" ) );
        }

        // ----------------------------------------------------------------------
        // Set the field names
        // ----------------------------------------------------------------------

        requirementOne.setFieldName( "one" );

        requirementTwo.setFieldName( "two" );

        Field one = composer.findMatchingField( component, descriptor, requirementOne, null );

        Field two = composer.findMatchingField( component, descriptor, requirementTwo, null );

        assertEquals( one.getName(), "one" );

        assertEquals( one.getDeclaringClass(), component.getClass() );

        assertEquals( two.getName(), "two" );

        assertEquals( two.getDeclaringClass(), component.getClass() );
    }
}
