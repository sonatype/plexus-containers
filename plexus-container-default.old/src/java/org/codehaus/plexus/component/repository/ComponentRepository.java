package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.configuration.Configuration;

import java.util.Map;
import java.util.List;

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

    boolean hasComponent( String role );

    boolean hasComponent( String role, String id );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    void addComponentDescriptor( Configuration configuration )
        throws ComponentRepositoryException;

    public ComponentDescriptor getComponentDescriptor( String role );

    public Map getComponentDescriptorMap( String role );

    public List getComponentDependencies( ComponentDescriptor componentDescriptor );

    // need to change this exception as not being able to find the class is but
    // only one validation problem. will do for now as i build up the tests.
    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException;
}
