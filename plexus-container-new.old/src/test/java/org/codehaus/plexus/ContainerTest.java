package org.codehaus.plexus;

import junit.framework.TestCase;
import org.codehaus.classworlds.ClassWorld;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ContainerTest
    extends TestCase
{
    public void testContainer()
        throws Exception
    {
        DefaultPlexusContainer container = new DefaultPlexusContainer();

        ClassWorld classWorld = new ClassWorld();

        container.setClassWorld( classWorld );

        try
        {
            container.getClassLoader();

            fail( "IllegalStateException should be thrown." );
        }
        catch ( IllegalStateException e )
        {
            // do nothing.0
        }
    }

}
