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
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.Map;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentManagerManager
{
    String ROLE = ComponentManagerManager.class.getName();

    void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    /**
     * same as {@link ComponentManagerManager#findComponentManagerByComponentKey(String, ClassRealm)}
     * where the 2nd param is the default lookup realm.
     */
    ComponentManager findComponentManagerByComponentKey( String componentKey );

    ComponentManager findComponentManagerByComponentKey( String componentKey, ClassRealm realm );

    ComponentManager findComponentManagerByComponentInstance( Object component );

    ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                             String componentKey )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException;

    Map getComponentManagers();

    void associateComponentWithComponentManager( Object component, ComponentManager componentManager );

    void unassociateComponentWithComponentManager( Object component );
}
