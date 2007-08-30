package org.codehaus.plexus;

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

import java.util.Collections;

import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.test.CountInstancesComponent;

import junit.framework.TestCase;

/**
 * Tests of the DefaultPlexusContaintr's child container properties
 * 
 * @author <a href="mailto:ptab@newitech.com">Piotr Tabor</a>
 */
public class DefaultPlexusContainerChildTest extends TestCase
{
    /* See PLX-350 */
    public void testCreateChildContainerClassRealm() throws PlexusContainerException, NoSuchRealmException
    {
        DefaultPlexusContainer parentContainer = new DefaultPlexusContainer();

        PlexusContainer childContainer = parentContainer.createChildContainer( "child1", createRealm() );

        assertNotNull( "Created child container expected to be not null", childContainer );

        assertEquals( "Wrong parent container", parentContainer,
                      ( (DefaultPlexusContainer) childContainer ).getParentContainer() );

        assertEquals( childContainer, parentContainer.getChildContainer( "child1" ) );
    }

    /* See PLX-350 */
    public void testCreateChildContainerUrl() throws PlexusContainerException, NoSuchRealmException
    {
        DefaultPlexusContainer parentContainer = new DefaultPlexusContainer();

        PlexusContainer childContainer = parentContainer.createChildContainer( "child1", Collections.EMPTY_SET );

        assertNotNull( "Created child container expected to be not null", childContainer );

        assertEquals( childContainer, parentContainer.getChildContainer( "child1" ) );

        assertEquals( "Wrong parent container", parentContainer,
                      ( (DefaultPlexusContainer) childContainer ).getParentContainer() );
    }

    /* See PLX-349 */
    public void test_doesChildUsesParentsSingletons()
        throws PlexusContainerException, ComponentLookupException, NoSuchRealmException
    {

        DefaultPlexusContainer container = new DefaultPlexusContainer();

        CountInstancesComponent.reset();

        assertNotNull( container.lookup( CountInstancesComponent.ROLE ) );

        assertEquals( "Single-instance class expected to be instanced once", 1,
                      CountInstancesComponent.getInstancesCount() );
        assertNotNull( container.createChildContainer( "childConteiner", createRealm() ).lookup(
                                                                                                 CountInstancesComponent.ROLE ) );
        assertEquals( "Single-instance class expected to be instanced once. ", 1,
                      CountInstancesComponent.getInstancesCount() );
    };

    protected ClassRealm createRealm() throws NoSuchRealmException
    {
        ClassWorld cw =
            new ClassWorld( "ChildContainerTestRealm1", DefaultPlexusContainerChildTest.class.getClassLoader() );
        ClassRealm realm = cw.getRealm( "ChildContainerTestRealm1" );
        assertNotNull( "Realm expected to be not null", realm );
        return realm;
    }

}
