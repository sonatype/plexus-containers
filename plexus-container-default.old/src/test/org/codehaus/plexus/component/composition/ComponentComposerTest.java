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

        composer.assignComponent( a, new DefaultComponentB() );

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
            composer.assignComponent( a, "foo" );
        }
        catch ( CompositionException e )
        {
            assertEquals( "No field which is compatible in component object.", e.getMessage() );
        }
    }

    public void testComponentAssignmentWithNullTarget()
        throws Exception
    {
        DefaultComponentComposer composer = new DefaultComponentComposer();

        DefaultComponentA a = null;

        try
        {
            composer.assignComponent( a, new DefaultComponentB() );
        }
        catch ( CompositionException e )
        {
            assertEquals( "Target object is null.", e.getMessage() );
        }
    }
}
