package org.codehaus.plexus.component.repository;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Like the avalon component manager. Central point to get the components from.
 * 
 * TODO: Enhance the ComponentRepository so that it can take entire 
 *       ComponentSetDescriptors instead of just ComponentDescriptors.
 */
public interface ComponentRepository
{
    void configure( PlexusConfiguration configuration );

    void initialize() throws ComponentRepositoryException;

    boolean hasComponent( String role );

    boolean hasComponent( String role, String id );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException;

    ComponentDescriptor getComponentDescriptor( String role );

    Map getComponentDescriptorMap( String role );

    List getComponentDependencies( ComponentDescriptor componentDescriptor );

    // need to change this exception as not being able to find the class is but
    // only one validation problem. will do for now as i build up the tests.
    void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException;

    void setClassRealm( ClassRealm classRealm );
}
