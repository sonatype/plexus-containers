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
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map.Entry;

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
    private static final String DEFAULT_COMPONENT_MANAGER_ID = "singleton";

    private final Map<Key, ComponentManager> activeComponentManagers = new TreeMap<Key, ComponentManager>();

    private final Map<String, Map<String, List<ComponentManager>>> roleIndex = new TreeMap<String, Map<String, List<ComponentManager>>>();

    private final Map<String, ComponentManager> componentManagers = new TreeMap<String, ComponentManager>();

    private LifecycleHandlerManager lifecycleHandlerManager;

    private final Map<Object, ComponentManager> componentManagersByComponent = new HashMap<Object, ComponentManager>();

    public synchronized void addComponentManager( ComponentManager componentManager )
    {
        componentManagers.put( componentManager.getId(), componentManager );
    }

    public synchronized LifecycleHandlerManager getLifecycleHandlerManager()
    {
        return lifecycleHandlerManager;
    }

    public synchronized void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
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
        // Create component manager for the new component
        ComponentManager componentManager = createComponentManager( descriptor.getInstantiationStrategy() );

        // Setup component manager
        LifecycleHandler lifecycleHandler = getLifecycleHandlerManager().getLifecycleHandler(
            descriptor.getLifecycleHandler() );
        componentManager.setup( container, lifecycleHandler, descriptor, role, roleHint );

        // Initialize component manager
        componentManager.initialize();

        // Add componentManager to indexes
        synchronized ( this )
        {
            Key key = new Key( descriptor.getRealmId(), role, roleHint );
            activeComponentManagers.put( key, componentManager );

            Map<String, List<ComponentManager>> hintIndex = roleIndex.get( key.role );
            if ( hintIndex == null )
            {
                hintIndex = new TreeMap<String, List<ComponentManager>>();
                roleIndex.put( key.role, hintIndex );
            }

            List<ComponentManager> components = hintIndex.get( key.roleHint );
            if ( components == null )
            {
                components = new ArrayList<ComponentManager>( 1 );
                hintIndex.put( key.roleHint, components );
            }

            components.add( componentManager );
        }

        return componentManager;
    }

    private ComponentManager createComponentManager( String id )
        throws UndefinedComponentManagerException
    {
        if ( id == null )
        {
            id = DEFAULT_COMPONENT_MANAGER_ID;
        }

        ComponentManager componentManager = componentManagers.get( id );

        if ( componentManager == null )
        {
            throw new UndefinedComponentManagerException( "Specified component manager cannot be found: " + id );
        }

        return componentManager.copy();
    }

    public synchronized ComponentManager findComponentManagerByComponentInstance( Object component )
    {
        return componentManagersByComponent.get( component );
    }

    public synchronized ComponentManager findComponentManagerByComponentKey( String role,
                                                                             String roleHint,
                                                                             ClassRealm realm )
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            throw new NullPointerException( "roleHint is null" );
        }

        // find the component
        if ( realm == null )
        {
            // find first registered component with role + roleHint in any realm
            Map<String, List<ComponentManager>> roleHintIndex = roleIndex.get( role );
            if ( roleHintIndex != null )
            {
                List<ComponentManager> componentManagers = roleHintIndex.get( roleHint );
                if ( componentManagers != null && !componentManagers.isEmpty() )
                {
                    ComponentManager componentManager = componentManagers.get( 0 );
                    return componentManager;
                }
            }
        }
        else
        {
            // find unique component registration using role, roleHint, realm
            for ( ; realm != null; realm = realm.getParentRealm() )
            {
                ComponentManager componentManager = activeComponentManagers.get(
                    new Key( realm.getId(), role, roleHint ) );
                if ( componentManager != null )
                {
                    return componentManager;
                }
            }
        }

        // component was not found
        return null;
    }

    public Map<String, ComponentManager> findAllComponentManagers( String role )
    {
        return findAllComponentManagers( role, null );
    }

    public synchronized Map<String, ComponentManager> findAllComponentManagers( String role, List<String> roleHints )
    {
        Map<String, ComponentManager> found = new LinkedHashMap<String, ComponentManager>();

        Map<String, List<ComponentManager>> roleHintIndex = roleIndex.get( role );
        if ( roleHintIndex != null )
        {
            if ( roleHints != null )
            {
                for ( String roleHint : roleHints )
                {
                    List<ComponentManager> components = roleHintIndex.get( roleHint );
                    if ( components != null && !components.isEmpty() )
                    {
                        found.put( roleHint, components.get( 0 ) );
                    }
                }
            }
            else
            {
                for ( Entry<String, List<ComponentManager>> entry : roleHintIndex.entrySet() )
                {
                    String roleHint = entry.getKey();
                    List<ComponentManager> components = entry.getValue();
                    if ( !components.isEmpty() )
                    {
                        found.put( roleHint, components.get( 0 ) );
                    }
                }
            }
        }

        return found;
    }

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    public void disposeAllComponents( Logger logger )
    {
        Collection<ComponentManager> componentManagers;
        synchronized ( this )
        {
            componentManagers = new ArrayList<ComponentManager>( activeComponentManagers.values() );
            activeComponentManagers.clear();
            roleIndex.clear();
        }

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager componentManager : componentManagers )
        {
            try
            {
                componentManager.dispose();
            }
            catch ( Exception e )
            {
                // todo dain use a monitor instead of a logger
                logger.error( "Error while disposing component manager. Continuing with the rest", e );
            }
        }
    }

    public synchronized void associateComponentWithComponentManager( Object component,
                                                                     ComponentManager componentManager )
    {
        componentManagersByComponent.put( component, componentManager );
    }

    public synchronized void unassociateComponentWithComponentManager( Object component )
    {
        componentManagersByComponent.remove( component );
    }

    public void dissociateComponentRealm( ClassRealm componentRealm )
        throws ComponentLifecycleException
    {
        List<ComponentManager> dispose = new ArrayList<ComponentManager>();

        synchronized ( this )
        {
            for ( Iterator<Entry<Key, ComponentManager>> it = activeComponentManagers.entrySet().iterator(); it.hasNext(); )
            {
                Entry<Key, ComponentManager> entry = it.next();
                Key key = entry.getKey();

                ComponentManager componentManager = entry.getValue();

                if ( key.realmId.equals( componentRealm.getId() ) )
                {
                    dispose.add( componentManager );
                    it.remove();

                    // remove component from role index
                    Map<String, List<ComponentManager>> roleHintIndex = roleIndex.get( key.role );
                    if ( roleHintIndex != null )
                    {
                        List<ComponentManager> components = roleHintIndex.get( key.roleHint );
                        if ( components != null )
                        {
                            components.remove( componentManager );
                        }
                    }
                }
                else
                {
                    componentManager.dissociateComponentRealm( componentRealm );
                }
            }
        }

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager componentManager : dispose )
        {
            componentManager.dispose();
        }
    }

    private static class Key implements Comparable<Key>
    {
        private final String realmId;
        private final String role;
        private final String roleHint;
        private final int hashCode;

        private Key( String realmId, String role, String roleHint )
        {
            if ( realmId == null )
            {
                realmId = "null";
            }
            this.realmId = realmId;

            if ( role == null )
            {
                role = "null";
            }
            this.role = role;

            if ( roleHint == null )
            {
                roleHint = "null";
            }
            this.roleHint = roleHint;

            int hashCode;
            hashCode = realmId.hashCode();
            hashCode = 31 * hashCode + role.hashCode();
            hashCode = 31 * hashCode + roleHint.hashCode();
            this.hashCode = hashCode;
        }

        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            Key key = (Key) o;

            return realmId.equals( key.realmId ) &&
                role.equals( key.role ) &&
                roleHint.equals( key.roleHint );

        }

        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            return realmId + "/" + role + "/" + roleHint;
        }

        public int compareTo( Key o )
        {
            int value = realmId.compareTo( o.realmId );
            if ( value == 0 )
            {
                value = role.compareTo( o.role );
                if ( value == 0 )
                {
                    value = roleHint.compareTo( o.roleHint );
                }
            }
            return value;
        }
    }
}
