package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

import java.util.Map;

/**
 * Like the avalon component manager. Central point to get the components from.
 *
 *
 */
public interface ComponentRepository
{
    void configure( Configuration configuration );

    void contextualize( Context context );

    void initialize()
        throws Exception;

    Object lookup( String role )
        throws ComponentLookupException;

    Map lookupAll( String role )
        throws ComponentLookupException;

    void releaseAll( Map components );

    Object lookup( String role, String id )
        throws ComponentLookupException;

    boolean hasService( String role );

    boolean hasService( String role, String id );

    void suspend( Object component );

    void resume( Object component );

    void release( Object service );

    void dispose();

    void setPlexusContainer( PlexusContainer container );

    ClassLoader getClassLoader();

    void enableLogging( Logger logger );

    void setComponentLogManager( LoggerManager logManager );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    void addComponentDescriptor( Configuration configuration )
        throws ComponentRepositoryException;
}
