package org.codehaus.plexus.component.manager;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
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
                                           ComponentDescriptor descriptor,
                                           ServiceManager service )
        throws Exception
    {
        ComponentManager componentManager = (ComponentManager) getInstance( componentManagerDescriptor.getImplementation(), classLoader );

        componentManager.enableLogging( loggerManager.getLogger( "component-manager" ) );
        componentManager.setClassLoader( classLoader );
        componentManager.configure( componentManagerDescriptor.getConfiguration() );
        componentManager.setLifecycleHandler( lifecycleHandler );
        componentManager.setComponentDescriptor( descriptor );
        
		if( componentManager instanceof Serviceable )
		{
			((Serviceable)componentManager).service(service);
		}		
		componentManager.initialize();
		
        return componentManager;


    }
}
