package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultComponetComposerTest extends TestCase
{

    public void testGetFieldByName()
    {
        ComponentF componentF = new ComponentF();
        final DefaultComponentComposer composer = new DefaultComponentComposer();
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

        //
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
        final DefaultComponentComposer composer = new DefaultComponentComposer();
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

        //
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

        final DefaultComponentComposer composer = new DefaultComponentComposer();

        try
        {
            composer.findMatchingField( componentF, componentDescriptor, requirementA   );
            composer.findMatchingField( componentF, componentDescriptor, requirementD   );
        }
        catch ( CompositionException e )
        {
            fail( e.getMessage() );
        }


    }

}
