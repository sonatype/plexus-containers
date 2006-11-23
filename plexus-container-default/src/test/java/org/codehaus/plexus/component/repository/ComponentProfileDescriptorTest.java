package org.codehaus.plexus.component.repository;

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

/**
 */
public class ComponentProfileDescriptorTest
    extends TestCase
{
    public ComponentProfileDescriptorTest( String name )
    {
        super( name );
    }

    public void testComponentProfileDescriptor()
        throws Exception
    {
        ComponentProfileDescriptor d = new ComponentProfileDescriptor();

        d.setComponentFactoryId( "cfid" );

        assertEquals( "cfid", d.getComponentFactoryId() );

        d.setLifecycleHandlerId( "lfhid" );

        assertEquals( "lfhid", d.getLifecycleHandlerId() );

        d.setComponentManagerId( "cmid" );

        assertEquals( "cmid", d.getComponentManagerId() );
    }
}
