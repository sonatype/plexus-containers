package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentComposerTest
    extends TestCase
{
    public void testComponentAssignment()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        ComponentA a = new ComponentA();

        composer.assignComponent( new ComponentB(), a );

        ComponentB b = a.getComponentB();

        assertNotNull( b );
    }

    public void testComponentAssignmentWithIncompatibleField()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        ComponentA a = new ComponentA();

        try
        {
            composer.assignComponent( "foo", a );
        }
        catch ( CompositionException e )
        {
            assertEquals( "No field which is compatible in target object.", e.getMessage() );
        }
    }

    public void testComponentAssignmentWithNullTarget()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        ComponentA a = null;

        try
        {
            composer.assignComponent( new ComponentB(), a );
        }
        catch ( CompositionException e )
        {
            assertEquals( "Target object is null.", e.getMessage() );
        }
    }
}
