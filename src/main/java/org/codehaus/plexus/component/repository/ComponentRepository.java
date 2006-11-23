package org.codehaus.plexus.component.repository;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;
import java.util.Map;

public interface ComponentRepository
{
    void configure( PlexusConfiguration configuration );

    void initialize()
        throws ComponentRepositoryException;

    boolean hasComponent( String role );

    boolean hasComponent( String role,
                          String id );

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
