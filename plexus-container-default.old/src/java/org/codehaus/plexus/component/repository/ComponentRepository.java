package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.Configuration;

import java.util.Map;

/**
 * Like the avalon component manager. Central point to get the components from.
 *
 *
 */
public interface ComponentRepository
{
    void configure( Configuration configuration );

    void initialize()
        throws Exception;

    boolean hasService( String role );

    boolean hasService( String role, String id );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    void addComponentDescriptor( Configuration configuration )
        throws ComponentRepositoryException;

    public ComponentDescriptor getComponentDescriptor( String role );

    public Map getComponentDescriptorMap( String role );
}
