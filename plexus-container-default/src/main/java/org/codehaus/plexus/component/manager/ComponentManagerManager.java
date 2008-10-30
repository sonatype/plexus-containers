package org.codehaus.plexus.component.manager;

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

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.Map;
import java.util.List;

/**
 *
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public interface ComponentManagerManager
{
    String ROLE = ComponentManagerManager.class.getName();

    void addComponentManager( ComponentManager componentManager );

    void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    ComponentManager findComponentManagerByComponentKey( String role, String roleHint, ClassRealm realm );

    Map<String, ComponentManager> findAllComponentManagers( String role );

    Map<String, ComponentManager> findAllComponentManagers( String role, List<String> roleHints );

    ComponentManager findComponentManagerByComponentInstance( Object component );

    ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                             String role )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException;

    ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                             String role, String roleHint )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException;

    void disposeAllComponents( Logger logger );

    void associateComponentWithComponentManager( Object component, ComponentManager componentManager );

    void unassociateComponentWithComponentManager( Object component );

    void dissociateComponentRealm( ClassRealm componentRealm )
        throws ComponentLifecycleException;
}
