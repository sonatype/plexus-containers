package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public class ComponentManagerFactory
    extends AbstractPlexusFactory
{
    public static ComponentManager create( ComponentDescriptor componentManagerDescriptor,
                                           LoggerManager loggerManager,
                                           ClassLoader classLoader,
                                           LifecycleHandler lifecycleHandler,
                                           ComponentDescriptor descriptor )
        throws Exception
    {
        ComponentManager componentManager = (ComponentManager) getInstance( componentManagerDescriptor.getImplementation(), classLoader );

        componentManager.enableLogging( loggerManager.getLogger( "manager-manager" ) );
        componentManager.setClassLoader( classLoader );
        componentManager.configure( componentManagerDescriptor.getConfiguration() );
        componentManager.setLifecycleHandler( lifecycleHandler );
        componentManager.setComponentDescriptor( descriptor );
        componentManager.initialize();

        return componentManager;
    }
}
