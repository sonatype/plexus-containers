package org.codehaus.plexus.component.factory;

import junit.framework.TestCase;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.classworlds.ClassWorld;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class JavaComponentFactoryTest
    extends TestCase
{
    public void testComponentCreation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplA.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        Object component = factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), null );

        assertNotNull( component );
    }


    public void testComponentCreationWithNotMatchingRoleAndImplemenation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

//        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplB.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), null );
    }
}
