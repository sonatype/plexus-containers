package org.codehaus.plexus.lifecycle.avalon.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.avalon.AvalonLifecycleHandler;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ServicePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager
                         )
        throws Exception
    {
        ServiceManager serviceManager =
            (ServiceManager) manager.getLifecycleHandler().getEntities().get( AvalonLifecycleHandler.SERVICE_MANAGER );

        if ( object instanceof Serviceable )
        {
            if ( null == serviceManager )
            {
                final String message = "ServiceManager is null";
                throw new IllegalArgumentException( message );
            }
            ( (Serviceable) object ).service( serviceManager );
        }
    }
}
