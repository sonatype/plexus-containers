package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.activity.Startable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class StopPhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();

        if ( object instanceof Startable )
        {
            ( (Startable) object ).stop();
        }
    }
}
