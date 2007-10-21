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
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Jason van Zyl
 *
 * @version $Id$
 */
public class DefaultComponentManagerManager
    implements ComponentManagerManager
{
    private Map activeComponentManagers = new HashMap();

    private Map componentManagers = null;

    private String defaultComponentManagerId = "singleton";

    private LifecycleHandlerManager lifecycleHandlerManager;

    private Map componentManagersByComponent = Collections.synchronizedMap( new HashMap() );

    public void addComponentManager( ComponentManager componentManager )
    {
        if ( componentManagers == null )
        {
            componentManagers = new HashMap();
        }

        componentManagers.put( componentManager.getId(), componentManager );
    }

    public void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
    }

    private ComponentManager copyComponentManager( String id )
        throws UndefinedComponentManagerException
    {
        ComponentManager componentManager = (ComponentManager) componentManagers.get( id );

        if ( componentManager == null )
        {
            throw new UndefinedComponentManagerException( "Specified component manager cannot be found: " + id );
        }

        return componentManager.copy();
    }


    public ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                                    String role )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException
    {
        return createComponentManager( descriptor, container, role, PlexusConstants.PLEXUS_DEFAULT_HINT );
    }

    public ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container,
                                                    String role, String roleHint )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException
    {
        String componentManagerId = descriptor.getInstantiationStrategy();

        ComponentManager componentManager;

        if ( componentManagerId == null )
        {
            componentManagerId = defaultComponentManagerId;
        }

        componentManager = copyComponentManager( componentManagerId );

        componentManager.setup( container, findLifecycleHandler( descriptor ), descriptor );

        componentManager.initialize();

        if ( StringUtils.equals( role, descriptor.getRole() ) && StringUtils.equals( roleHint, descriptor.getRoleHint() ) )
        {
            activeComponentManagers.put( descriptor.getRealmId() + "/" + descriptor.getRole() + "/" + descriptor.getRoleHint(), componentManager );
        }
        else
        {
            activeComponentManagers.put( descriptor.getRealmId() + "/" + role + "/" + roleHint, componentManager );
        }

        return componentManager;
    }

    public ComponentManager findComponentManagerByComponentInstance( Object component )
    {
        return (ComponentManager) componentManagersByComponent.get( component );
    }

    public ComponentManager findComponentManagerByComponentKey( String role, String roleHint, ClassRealm lookupRealm )
    {
        while ( lookupRealm != null )
        {
            ComponentManager mgr = (ComponentManager) activeComponentManagers.get( lookupRealm.getId() + "/" + role + "/" + roleHint );

            if ( mgr != null )
            {
                return mgr;
            }

            lookupRealm = lookupRealm.getParentRealm();
        }

        return null;
    }


    // ----------------------------------------------------------------------
    // Lifecycle handler manager handling
    // ----------------------------------------------------------------------

    private LifecycleHandler findLifecycleHandler( ComponentDescriptor descriptor )
        throws UndefinedLifecycleHandlerException
    {
        String lifecycleHandlerId = descriptor.getLifecycleHandler();

        LifecycleHandler lifecycleHandler;

        if ( lifecycleHandlerId == null )
        {
            lifecycleHandler = lifecycleHandlerManager.getDefaultLifecycleHandler();
        }
        else
        {
            lifecycleHandler = lifecycleHandlerManager.getLifecycleHandler( lifecycleHandlerId );
        }

        return lifecycleHandler;
    }

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    public Map getComponentManagers()
    {
        return activeComponentManagers;
    }

    public void associateComponentWithComponentManager( Object component, ComponentManager componentManager )
    {
        componentManagersByComponent.put( component, componentManager );
    }

    public void unassociateComponentWithComponentManager( Object component )
    {
        componentManagersByComponent.remove( component );
    }
}
