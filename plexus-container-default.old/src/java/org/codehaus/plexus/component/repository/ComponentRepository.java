package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * Like the avalon component manager. Central point to get the components from.
 *
 *
 */
public interface ComponentRepository
{
    void configure( Configuration configuration );

    void contextualize( Context context );

    /**
     * Initialize this repository
     * @throws Exception
     */
    void initialize()
        throws Exception;

    /**
     * Lookup the component with the given role
     *
     * @param role
     * @return
     * @throws ServiceException if no component with the given role exists, or there was an
     * error taking the component through a lifecycle
     */
    Object lookup( String role )
        throws ServiceException;

    Object lookup( String role, String id )
        throws ServiceException;

    /**
     * Test if this repository manages the component with the given role
     *
     * @param role
     * @return
     */
    boolean hasService( String role );

    /**
     * Test if this repository manages the component with the given role
     * and id
     *
     * @param role
     * @return
     */
    boolean hasService( String role, String id );

    void suspend( Object component );

    void resume( Object component );

    void release( Object service );

    void dispose();

    void setPlexusContainer( PlexusContainer container );

    /**
     * Return the number of instantiated components
     * @return
     */
    int getComponentCount();

    ClassLoader getClassLoader();

    /** Set this repositories logger */
    void enableLogging( Logger logger );

    /** Set the logManager to be used for components */
    void setComponentLogManager( LoggerManager logManager );
}
