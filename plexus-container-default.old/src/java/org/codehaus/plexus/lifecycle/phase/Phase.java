package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public interface Phase
{
    /** Execute the phase. */
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception;
}
