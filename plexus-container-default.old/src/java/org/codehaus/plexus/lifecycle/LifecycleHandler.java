package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.service.repository.ComponentHousing;

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
    public static String SERVICE_REPOSITORY = "service.repository";

    /** Set lifecycle specifics.
     *
     *  <p>The lifecycle of a particular service may vary from implementation
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

    /** Start lifecycle.
     *
     */
    void startLifecycle( ComponentHousing housing )
        throws Exception;

    /** End Lifecycle
     *
     */
    void endLifecycle( ComponentHousing housing )
        throws Exception;

    void initialize()
        throws Exception;

    void addBeginSegmentPhase( Phase phase );

    void addEndSegmentPhase( Phase phase );

    void enableLogging( Logger logger );

    public void configure( Configuration config ) throws ConfigurationException;

}
