package org.codehaus.plexus.lifecycle.avalon.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.apache.avalon.framework.activity.Suspendable;

public class ResumePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Suspendable )
        {
            ( (Suspendable) object ).resume();
        }
    }
}
