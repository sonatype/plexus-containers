package org.codehaus.plexus.component.repository;

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
