package org.codehaus.plexus;

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

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.manager.ComponentManagerFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 * @author Dain Sundstrom
 */
public interface ComponentRegistry
{
    void registerComponentManagerFactory( ComponentManagerFactory componentManagerFactory );

    void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor )
        throws CycleDetectedInComponentGraphException;

    <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String role, String roleHint );

    @Deprecated
    ComponentDescriptor<?> getComponentDescriptor( String role, String roleHint, ClassRealm realm );

    <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type, String role );

    <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type, String role );

    <T> T lookup( Class<T> type, String role, String roleHint )
        throws ComponentLookupException;

    <T> T lookup( ComponentDescriptor<T> componentDescriptor )
        throws ComponentLookupException;

    <T> List<T> lookupList( Class<T> type, String role, List<String> hints )
        throws ComponentLookupException;

    <T> Map<String, T> lookupMap( Class<T> type, String role, List<String> hints )
        throws ComponentLookupException;

    void release( Object component ) throws ComponentLifecycleException;

    void removeComponentRealm( ClassRealm classRealm ) throws PlexusContainerException;

    void dispose();

    <T> void addComponent( T component, String role, String roleHint );
}