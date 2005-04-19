package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public abstract class AbstractPhase
    implements Phase
{
    /** Execute the phase. */
    public abstract void execute( Object component, ComponentManager manager )
        throws PhaseExecutionException;
}
