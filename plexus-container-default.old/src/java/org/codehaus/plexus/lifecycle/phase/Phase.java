package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;

public interface Phase
{
    /** Execute the phase. */
    public void execute( Object component, ComponentManager manager )
        throws Exception;
}
