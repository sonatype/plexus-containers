package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public abstract class AbstractPhase
    implements Phase
{
    /** Execute the phase. */
    public abstract void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception;
}
