package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.apache.avalon.framework.activity.Suspendable;

public class SuspendPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Suspendable )
        {
            ( (Suspendable) object ).suspend();
        }
    }
}
