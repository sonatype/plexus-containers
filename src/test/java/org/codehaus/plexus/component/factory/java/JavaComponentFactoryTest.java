package org.codehaus.plexus.component.factory.java;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.Component;
import org.codehaus.plexus.component.factory.ComponentImplA;
import org.codehaus.plexus.component.factory.ComponentImplB;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import junit.framework.TestCase;

import java.util.AbstractMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class JavaComponentFactoryTest
    extends PlexusTestCase
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

        Object component = factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), getContainer() );

        assertNotNull( component );
    }

    public void testComponentCreationWithNotMatchingRoleAndImplemenation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplB.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), getContainer() );
    }

    public void testThatTheContainerThrowsAnExceptionWhenAnAttemptIsMadeToInstantiateAnAbstractClass()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( AbstractMap.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        try
        {
            factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), getContainer() );

            fail( "Expected ComponentInstantiationException when instaniating a abstract class." );
        }
        catch( ComponentInstantiationException ex )
        {
            // expected
        }
    }

    public void testThatTheContainerThrowsAnExceptionWhenAnAttemptIsMadeToInstantiateAnInterface()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( Map.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        try
        {
            factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), getContainer() );

            fail( "Expected ComponentInstantiationException when instantiating an interface." );
        }
        catch( ComponentInstantiationException ex )
        {
            // expected
        }
    }

}
