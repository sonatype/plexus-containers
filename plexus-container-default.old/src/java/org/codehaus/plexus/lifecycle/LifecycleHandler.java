package org.codehaus.plexus.lifecycle;


import org.codehaus.plexus.component.manager.ComponentManager;

import java.util.Map;

public interface LifecycleHandler
{
    void addEntity( String key, Object entity );

    Map getEntities();

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
