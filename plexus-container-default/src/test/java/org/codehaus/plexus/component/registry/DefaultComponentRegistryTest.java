package org.codehaus.plexus.component.registry;

import junit.framework.TestCase;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class DefaultComponentRegistryTest
    extends TestCase
{

    public void testConcurrentDisposeAndLookup()
        throws Exception
    {
        ComponentDescriptor<TestSynchronizedComponent> descriptor = new ComponentDescriptor<TestSynchronizedComponent>(
            TestSynchronizedComponent.class );
        descriptor.setRole( TestSynchronizedComponent.class.getCanonicalName() );
        descriptor.setImplementation( TestSynchronizedComponent.class.getCanonicalName() );

        final PlexusContainer plexus = new DefaultPlexusContainer();
        plexus.addComponentDescriptor( descriptor );

        TestSynchronizedComponent component = plexus.lookup( TestSynchronizedComponent.class );
        
        class LookupThread extends Thread
        {
            private TestSynchronizedComponent component;

            @Override
            public synchronized void run()
            {
                try
                {
                    this.component =  plexus.lookup( TestSynchronizedComponent.class );
                }
                catch ( ComponentLookupException e )
                {
                    // expected
                }
            }

            public synchronized TestSynchronizedComponent getComponent()
            {
                return component;
            }
        };

        LookupThread lookupThread = new LookupThread();

        component.setLookupThread( lookupThread );

        plexus.dispose();

        assertNull( lookupThread.getComponent() );
    }

}
