package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class ServicePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager
                         )
        throws Exception
    {
        ServiceManager serviceManager = (ServiceManager) manager.getLifecycleHandler().getEntities().get( "component.manager" );

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
