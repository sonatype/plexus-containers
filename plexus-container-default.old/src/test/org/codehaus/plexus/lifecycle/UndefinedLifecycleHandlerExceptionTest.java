package org.codehaus.plexus.lifecycle;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.ConfigurationResourceException;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class UndefinedLifecycleHandlerExceptionTest
    extends TestCase
{
    public void testException()
    {
        UndefinedLifecycleHandlerException e = new UndefinedLifecycleHandlerException( "bad doggy!" );

        assertEquals( "bad doggy!", e.getMessage() );
    }
}
