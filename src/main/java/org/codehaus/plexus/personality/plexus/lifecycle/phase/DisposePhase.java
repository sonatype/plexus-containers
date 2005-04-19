package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class DisposePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
    {
        if ( object instanceof Disposable )
        {
            ( (Disposable) object ).dispose();
        }
    }
}
