package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.activity.Disposable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class DisposePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();

        if ( object instanceof Disposable )
        {
            ( (Disposable) object ).dispose();
        }
    }
}
