package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class ServicePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        ServiceManager serviceManager = (ServiceManager) handler.getEntities().get( "service.manager" );

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
