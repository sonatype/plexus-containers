package org.codehaus.plexus.component.discovery;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.context.DefaultContext;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public class ComponentDiscovererTest
    extends PlexusTestCase
{
    public void testDefaultComponentDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        componentDiscoverer.setManager( new DefaultComponentDiscovererManager() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm core = classWorld.newRealm( "core" );

        File testClasses = new File( getBasedir(), "target/test-classes" );

        core.addURL( testClasses.toURL() );

        File classes = new File( getBasedir(), "target/classes" );

        core.addURL( classes.toURL() );

        List<ComponentSetDescriptor> componentSetDescriptors = componentDiscoverer.findComponents( new DefaultContext(), core );

        ComponentDescriptor<?> componentDescriptor = byImplementation(componentSetDescriptors).get( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent" );

        assertNotNull("componentDescriptor is null", componentDescriptor );

        assertEquals( "org.codehaus.plexus.component.discovery.DiscoveredComponent", componentDescriptor.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent", componentDescriptor.getImplementation() );
    }

    private static Map<String, ComponentDescriptor<?>> byImplementation(List<ComponentSetDescriptor> descriptorSets) {
        TreeMap<String, ComponentDescriptor<?>> index = new TreeMap<String, ComponentDescriptor<?>>();
        for ( ComponentSetDescriptor descriptorSet : descriptorSets )
        {
            for ( ComponentDescriptor<?> descriptor : descriptorSet.getComponents() )
            {
                index.put(descriptor.getImplementation(), descriptor);
            }
        }
        return index;
    }
}
