package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.activity.Startable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class StopPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Startable )
        {
            ( (Startable) object ).stop();
        }
    }
}
