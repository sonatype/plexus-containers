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

        DefaultComponentA a = new DefaultComponentA();

        composer.assignComponent( new DefaultComponentB(), a );

        DefaultComponentB b = a.getComponentB();

        assertNotNull( b );
    }

    public void testComponentAssignmentWithIncompatibleField()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        DefaultComponentA a = new DefaultComponentA();

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

        DefaultComponentA a = null;

        try
        {
            composer.assignComponent( new DefaultComponentB(), a );
        }
        catch ( CompositionException e )
        {
            assertEquals( "Target object is null.", e.getMessage() );
        }
    }
}
