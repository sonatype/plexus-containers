package org.codehaus.plexus.component.factory;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

public class DiscoveredComponentFactoryTest
    extends PlexusTestCase
{

    public void testShouldFindComponentFactoriesDefinedInBothPlexusXmlAndComponentsXml()
        throws Exception
    {
        assertNotNull( "Cannot find test component factory from plexus.xml test resource.",
                       lookup( ComponentFactory.ROLE, "testFactory1" ) );

        assertNotNull( "Cannot find test component factory from components.xml test resource.",
                       lookup( ComponentFactory.ROLE, "testFactory2" ) );
    }

    public void testShouldInstantiateComponentUsingFactoryDiscoveredInPlexusXml()
        throws Exception
    {
        lookupTestComponent( "testFactory1" );
    }

    public void testShouldInstantiateComponentUsingFactoryDiscoveredInComponentsXml()
        throws Exception
    {
        lookupTestComponent( "testFactory2" );
    }

    private void lookupTestComponent( String factoryId )
        throws Exception
    {
        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setComponentFactory( factoryId );
        descriptor.setRole( "role" );
        descriptor.setRoleHint( "hint" );
        descriptor.setImplementation( "something interesting" );

        getContainer().addComponentDescriptor( descriptor );

        Object component = lookup( "role", "hint" );

        assertTrue( component instanceof TestFactoryResultComponent );
        assertEquals( factoryId, ( (TestFactoryResultComponent) component ).getFactoryId() );
    }

}
