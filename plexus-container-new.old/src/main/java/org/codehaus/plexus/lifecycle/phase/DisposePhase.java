package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.activity.Disposable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class DisposePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Disposable )
        {
            ( (Disposable) object ).dispose();
        }
    }
}
