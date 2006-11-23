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
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.manager.ClassicSingletonComponentManager;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandler;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentProfileTest
    extends TestCase
{
    public void testComponentProfile()
    {
        ComponentProfile profile = new ComponentProfile();

        ComponentFactory componentFactory = new JavaComponentFactory();

        LifecycleHandler lifecycleHandler = new MockLifecycleHandler();

        ComponentManager componentManager = new ClassicSingletonComponentManager();

        profile.setComponentFactory( componentFactory );

        assertEquals( componentFactory, profile.getComponentFactory() );

        profile.setLifecycleHandler( lifecycleHandler );

        assertEquals( lifecycleHandler, profile.getLifecycleHandler() );

        profile.setComponentManager( componentManager );

        assertEquals( componentManager, profile.getComponentManager() );
    }

    class MockLifecycleHandler
        extends AbstractLifecycleHandler
    {
        public void initialize()
        {
        }
    }
}
