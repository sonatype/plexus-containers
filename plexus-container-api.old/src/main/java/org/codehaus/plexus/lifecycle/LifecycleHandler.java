package org.codehaus.plexus.lifecycle;


import org.codehaus.plexus.component.manager.ComponentManager;

public interface LifecycleHandler
{
    String getId();

    void start( Object component, ComponentManager manager )
        throws Exception;

    void suspend( Object component, ComponentManager manager )
        throws Exception;

    void resume( Object component, ComponentManager manager )
        throws Exception;

    void end( Object component, ComponentManager manager )
        throws Exception;

    void initialize()
        throws Exception;
}
