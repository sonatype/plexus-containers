package org.codehaus.plexus.component.collections;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

/** @author Jason van Zyl */

// We need to have the collection notified when a new implementation of a given role has
// been added to the container. We probably need some options so that we know when new
// component descriptors have been added to the system, and an option to keep the collection
// up-to-date when new implementations are added.
//
// NOTE: This includes component additions, but also component purges from the
// container, as when a component realm is disposed
// (and PlexusContainer.removeComponentRealm(..) is called).
public abstract class AbstractComponentCollection<T>
{
    /** The reference to the PlexusContainer */
    protected MutablePlexusContainer container;

    /** The type of the components held by this collection*/
    protected final Class<T> componentType;

    /** The role of the components we are holding in this Collection. */
    protected String role;

    /** The role hint of the components we are holding in this Collection. */
    protected List<String> roleHints;

    /** The component that requires this collection of components */
    protected String hostComponent;

    /** Used to log errors in the component lookup process. */
    protected Logger logger;

    private ClassLoader tccl;
    private Collection<ClassRealm> realms;

    private Map<String, ComponentDescriptor<T>> componentDescriptorMap;
    private final ClassWorld world;

    public AbstractComponentCollection( final MutablePlexusContainer container,
                                        final Class<T> componentType,
                                        final String role,
                                        final List<String> roleHints,
                                        final String hostComponent )
    {
        this.container = container;

        this.componentType = componentType;

        this.role = role;

        this.roleHints = roleHints;

        this.hostComponent = hostComponent;

        logger = container.getLoggerManager().getLoggerForComponent( role );

        world = container.getContainerRealm().getWorld();
    }

    private boolean realmsHaveChanged()
    {
        return ( tccl != Thread.currentThread().getContextClassLoader() ) ||
               ( realms == null ) || ( !realms.equals( world.getRealms() ) );
    }

    protected synchronized Map<String, ComponentDescriptor<T>> getComponentDescriptorMap()
    {
        checkUpdate();

        return componentDescriptorMap;
    }

    @SuppressWarnings( "unchecked" )
    protected boolean checkUpdate()
    {
        if ( componentDescriptorMap != null && !realmsHaveChanged() )
        {
            return false;
        }

        tccl = Thread.currentThread().getContextClassLoader();
        Collection fromWorld = world.getRealms();
        if ( fromWorld == null || fromWorld.isEmpty() )
        {
            realms = null;
        }
        else
        {
            realms = new HashSet<ClassRealm>( fromWorld );
        }

        Map<String, ComponentDescriptor<T>> componentMap = container.getComponentDescriptorMap( componentType, role );
        Map<String, ComponentDescriptor<T>> newComponentDescriptors =
            new HashMap<String, ComponentDescriptor<T>>( componentMap.size() * 2 );

        if ( roleHints != null && !roleHints.isEmpty() )
        {
            for ( String roleHint : roleHints )
            {
                ComponentDescriptor<T> componentDescriptor = componentMap.get( roleHint );
                if ( componentDescriptor != null )
                {
                    newComponentDescriptors.put( roleHint, componentDescriptor );
                }
            }
        }
        else
        {
            newComponentDescriptors.putAll( componentMap );
        }

        if ( componentDescriptorMap == null || !newComponentDescriptors.equals( componentDescriptorMap ) )
        {
            componentDescriptorMap = newComponentDescriptors;

            return true;
        }

        return false;
    }

    protected T lookup( ComponentDescriptor<T> componentDescriptor )
    {
        T component = null;

        try
        {
            if ( componentDescriptor != null )
            {
                component = container.lookup( componentDescriptor );
            }
        }
        catch ( ComponentLookupException e )
        {
            logger.debug( "Failed to lookup a member of active collection with role: " + role + " and role-hint: "
                + componentDescriptor.getRoleHint(), e );
        }

        return component;
    }

    public synchronized void clear()
    {
        releaseAllCallback();

        componentDescriptorMap = null;

        tccl = null;
        realms = null;
    }

    protected abstract void releaseAllCallback();

}
