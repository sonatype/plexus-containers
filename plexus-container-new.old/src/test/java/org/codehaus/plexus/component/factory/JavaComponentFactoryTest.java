package org.codehaus.plexus.component.factory;

import junit.framework.TestCase;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class JavaComponentFactoryTest
    extends TestCase
{
    public void testComponentCreation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Object component = factory.newInstance( "org.codehaus.plexus.component.factory.DefaultComponent", cl );

        assertNotNull( component );
    }
}
