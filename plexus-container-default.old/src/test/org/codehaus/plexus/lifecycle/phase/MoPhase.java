package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class MoPhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();

        if ( object instanceof Mo )
        {
            ( (Mo) object ).mo();
        }
    }
}
