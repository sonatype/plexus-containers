package org.codehaus.plexus.lifecycle;


import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public interface LifecycleHandler
{
    String getId();

    void start( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void suspend( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void resume( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void end( Object component, ComponentManager manager )
        throws PhaseExecutionException;

    void initialize();
}
