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
import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;
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
import java.util.SortedMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ListMultimap;

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
    private static final String DEFAULT_INSTANTIATION_STRATEGY = "singleton";

    private final Map<Key, ComponentManager<?>> activeComponentManagers = new TreeMap<Key, ComponentManager<?>>();

    private final Map<ClassRealm, SortedMap<String, ListMultimap<String, ComponentManager<?>>>> index = new LinkedHashMap<ClassRealm, SortedMap<String, ListMultimap<String, ComponentManager<?>>>>();

    private final Map<String, ComponentManagerFactory> componentManagerFactories = new TreeMap<String, ComponentManagerFactory>();

    private LifecycleHandlerManager lifecycleHandlerManager;

    private final Map<Object, ComponentManager<?>> componentManagersByComponent = new HashMap<Object, ComponentManager<?>>();

    public synchronized void addComponentManagerFactory( ComponentManagerFactory componentManagerFactory )
    {
        componentManagerFactories.put( componentManagerFactory.getId(), componentManagerFactory );
    }

    public synchronized LifecycleHandlerManager getLifecycleHandlerManager()
    {
        return lifecycleHandlerManager;
    }

    public synchronized void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager )
    {
        this.lifecycleHandlerManager = lifecycleHandlerManager;
    }

    public ListMultimap<String, ComponentManager<?>> getComponentManagers( String role )
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // determine realms to search
        LinkedHashSet<ClassRealm> realms = new LinkedHashSet<ClassRealm>();
        for (ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
            if ( classLoader instanceof ClassRealm )
            {
                ClassRealm realm = (ClassRealm) classLoader;
                while (realm != null) {
                    realms.add(realm);
                    realm = realm.getParentRealm();
                }
            }
        }
        if (realms.isEmpty()) {
            realms.addAll( index.keySet() );
        }

        // Get all valid component managers
        ListMultimap<String, ComponentManager<?>> roleHintIndex = Multimaps.newArrayListMultimap();
        for ( ClassRealm realm : realms )
        {
            SortedMap<String, ListMultimap<String, ComponentManager<?>>> roleIndex = index.get( realm );
            if (roleIndex != null) {
                Multimap<String, ComponentManager<?>> managers = roleIndex.get( role );
                if ( managers != null )
                {
                    roleHintIndex.putAll( managers );
                }
            }
        }
        return Multimaps.unmodifiableListMultimap( roleHintIndex );
    }

    public <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor, MutablePlexusContainer container,
                                                    String role )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException
    {
        return createComponentManager( descriptor, container, role, PlexusConstants.PLEXUS_DEFAULT_HINT );
    }

    public <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor,
                                                           MutablePlexusContainer container,
                                                           String role,
                                                           String roleHint )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException
    {
        // Setup component manager
        LifecycleHandler lifecycleHandler = getLifecycleHandlerManager().getLifecycleHandler( descriptor.getLifecycleHandler() );
        ComponentManager<T> componentManager = createComponentManager( container, lifecycleHandler, descriptor, role, roleHint );

        // Add componentManager to indexes
        synchronized ( this )
        {
            Key key = new Key( descriptor.getRealm(), role, roleHint );
            activeComponentManagers.put( key, componentManager );

            SortedMap<String, ListMultimap<String, ComponentManager<?>>> roleIndex = index.get( descriptor.getRealm() );
            if (roleIndex == null) {
                roleIndex = new TreeMap<String, ListMultimap<String, ComponentManager<?>>>();
                index.put(descriptor.getRealm(), roleIndex);
            }

            ListMultimap<String, ComponentManager<?>> hintIndex = roleIndex.get( key.role );
            if ( hintIndex == null )
            {
                hintIndex = Multimaps.newArrayListMultimap(  );
                roleIndex.put( key.role, hintIndex );
            }

            hintIndex.put(key.roleHint, componentManager);
        }

        return componentManager;
    }

    private <T> ComponentManager<T> createComponentManager( MutablePlexusContainer container,
                                                     LifecycleHandler lifecycleHandler,
                                                     ComponentDescriptor<T> descriptor,
                                                     String role,
                                                     String roleHint
    )
        throws UndefinedComponentManagerException
    {


        String instantiationStrategy = descriptor.getInstantiationStrategy();
        if (instantiationStrategy == null)
        {
            instantiationStrategy = DEFAULT_INSTANTIATION_STRATEGY;
        }

        ComponentManagerFactory componentManagerFactory = componentManagerFactories.get( instantiationStrategy );

        if (componentManagerFactories == null)
        {
            throw new UndefinedComponentManagerException( "Unsupported instantiation strategy: " + instantiationStrategy );
        }

        // Create component manager for the new component
        ComponentManager<T> componentManager =  componentManagerFactory.createComponentManager( container,
            lifecycleHandler,
            descriptor,
            role,
            roleHint );

        return componentManager;
    }

    public synchronized <T> ComponentManager<T> findComponentManagerByComponentInstance( T component )
    {
        ComponentManager<?> componentManager = componentManagersByComponent.get( component );
        return (ComponentManager<T>) componentManager;
    }

    public synchronized <T> ComponentManager<T> findComponentManager( Class<T> type, String role, String roleHint )
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            throw new NullPointerException( "roleHint is null" );
        }

        // find the component

        // find first registered component with role + roleHint in any realm
        Multimap<String, ComponentManager<?>> roleHintIndex = getComponentManagers( role );

        // select a component that can be cast to the specified type
        for ( ComponentManager<?> componentManager : roleHintIndex.get( roleHint ) )
        {
            if ( isAssignableFrom( type, componentManager.getType() )) {
                return (ComponentManager<T>) componentManager;
            }
        }

        // component was not found
        return null;
    }

    public Map<String, ComponentManager<?>> findAllComponentManagers( String role )
    {
        return findAllComponentManagers( role, null );
    }

    public synchronized Map<String, ComponentManager<?>> findAllComponentManagers( String role, List<String> roleHints )
    {
        Map<String, ComponentManager<?>> found = new LinkedHashMap<String, ComponentManager<?>>();

        ListMultimap<String, ComponentManager<?>> roleHintIndex = getComponentManagers( role );
        if ( roleHints != null )
        {
            for ( String roleHint : roleHints )
            {
                List<ComponentManager<?>> componentManagers = roleHintIndex.get( roleHint );
                if (!componentManagers.isEmpty()) {
                    found.put( roleHint, componentManagers.get(0) );
                }
            }
        }
        else
        {
            for ( Entry<String, ComponentManager<?>> entry : roleHintIndex.entries() )
            {
                String roleHint = entry.getKey();
                ComponentManager<?> manager = entry.getValue();
                if (!found.containsKey( roleHint )) {
                    found.put(roleHint, manager);
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
        Collection<ComponentManager<?>> componentManagers;
        synchronized ( this )
        {
            componentManagers = new ArrayList<ComponentManager<?>>( activeComponentManagers.values() );
            activeComponentManagers.clear();
            index.clear();
        }

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager<?> componentManager : componentManagers )
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

    public synchronized <T> void associateComponentWithComponentManager( T component,
                                                                     ComponentManager<T> componentManager )
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
        List<ComponentManager<?>> dispose = new ArrayList<ComponentManager<?>>();

        synchronized ( this )
        {
            for ( Iterator<Entry<Key, ComponentManager<?>>> it = activeComponentManagers.entrySet().iterator(); it.hasNext(); )
            {
                Entry<Key, ComponentManager<?>> entry = it.next();
                Key key = entry.getKey();

                ComponentManager<?> componentManager = entry.getValue();

                if ( key.realm.equals( componentRealm ) )
                {
                    dispose.add( componentManager );
                    it.remove();
                }
                else
                {
                    componentManager.dissociateComponentRealm( componentRealm );
                }
            }
            index.remove(componentRealm);
        }

        // Call dispose callback outside of synchronized lock to avoid deadlocks
        for ( ComponentManager<?> componentManager : dispose )
        {
            componentManager.dispose();
        }
    }

    private static class Key implements Comparable<Key>
    {
        private final ClassRealm realm;
        private final String role;
        private final String roleHint;
        private final int hashCode;

        private Key( ClassRealm realm, String role, String roleHint )
        {
            this.realm = realm;

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
            hashCode = ( realm != null ? realm.hashCode() : 0 );
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

            return !( realm != null ? !realm.equals( key.realm ) : key.realm != null ) &&
                role.equals( key.role ) &&
                roleHint.equals( key.roleHint );

        }

        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            return realm + "/" + role + "/" + roleHint;
        }

        public int compareTo( Key o )
        {
            int value;
            if (realm != null) {
                value = realm.getId().compareTo( o.realm.getId() );
            } else {
                value = o.realm == null ? 0 : 1;
            }

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
