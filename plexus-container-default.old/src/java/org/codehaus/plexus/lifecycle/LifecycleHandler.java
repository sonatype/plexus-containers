package org.codehaus.plexus.lifecycle;


import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.logging.Logger;

import java.util.Map;

public interface LifecycleHandler
{
    public static String LOGGER = "logger";

    public static String CONTEXT = "context";

    public static String COMPONENT_REPOSITORY = "component.repository";

    public static String PLEXUS_CONTAINER = "plexus.container";

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

    void enableLogging( Logger logger );
}
