package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class CompositionExceptionTest
    extends TestCase
{
    public void testException()
    {
        CompositionException e = new CompositionException( "bad doggy!" );

        assertEquals( "bad doggy!", e.getMessage() );
    }
}
