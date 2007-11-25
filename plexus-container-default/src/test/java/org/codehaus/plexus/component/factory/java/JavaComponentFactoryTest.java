package org.codehaus.plexus.component.factory.java;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.factory.Component;
import org.codehaus.plexus.component.factory.ComponentImplA;
import org.codehaus.plexus.component.factory.ComponentImplB;
import org.codehaus.plexus.component.factory.ComponentImplC;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * @author Jason van Zyl
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

        PlexusContainer container = new DefaultPlexusContainer( containerConfiguration( classWorld ));

        Object component = factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), container );

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

        PlexusContainer container = new DefaultPlexusContainer( containerConfiguration( classWorld ));

        factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), container );
    }

    public void testInstanciationOfAAbstractComponent()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplC.class.getName() );

        ClassWorld classWorld = new ClassWorld();

        classWorld.newRealm( "core", Thread.currentThread().getContextClassLoader() );

        PlexusContainer container = new DefaultPlexusContainer( containerConfiguration( classWorld ) );

        try
        {
            factory.newInstance( componentDescriptor, classWorld.getRealm( "core" ), container );

            fail( "Expected ComponentInstantiationException when instanciating a abstract class." );
        }
        catch( ComponentInstantiationException ex )
        {
            assertTrue( true );
        }
    }

    private ContainerConfiguration containerConfiguration( ClassWorld classWorld )
    {
        ContainerConfiguration c = new DefaultContainerConfiguration()
            .setClassWorld( classWorld );

        return c;
    }
}
