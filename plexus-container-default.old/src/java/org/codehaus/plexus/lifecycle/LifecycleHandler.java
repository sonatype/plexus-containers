package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.Phase;

import java.util.Map;

/**
 * Takes a component through it's lifecycle.
 *
 *
 */
public interface LifecycleHandler
{
    /** */
    public static String LOGGER = "logger";
    /** */
    public static String CONTEXT = "context";
    /** */
    public static String SERVICE_REPOSITORY = "component.repository";

    /** Set lifecycle specifics.
     *
     *  <p>The lifecycle of a particular component may vary from implementation
     *  to implementation.</p>
     *
     * <p>These entities are made available to the various phases, are are global
     * to the Lifecycel handler (no component specific stuff in here)</p>
     *
     */
    void addEntity( String key, Object entity );

    /** Get lifecycle entities.
     *
     * @return
     */
    Map getEntities();

    String getId();

    void startLifecycle( Object component, ComponentManager manager )
        throws Exception;

    void suspend( Object component, ComponentManager manager )
        throws Exception;

    void resume( Object component, ComponentManager manager )
        throws Exception;

    void endLifecycle( Object component, ComponentManager manager )
        throws Exception;

    void initialize()
        throws Exception;

    void enableLogging( Logger logger );
}
