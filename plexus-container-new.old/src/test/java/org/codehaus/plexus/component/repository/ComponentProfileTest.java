package org.codehaus.plexus.component.repository;

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
