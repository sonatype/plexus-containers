package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;

public abstract class AbstractPhase
    implements Phase
{
    /** Execute the phase. */
    public abstract void execute( Object component, ComponentManager manager )
        throws Exception;
}
