package org.codehaus.plexus.component.collections;

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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl FIXME: [jdcasey] We need to review the efficiency (in speed and memory) of this collection...
 */
public class ComponentMap<T>
    extends AbstractComponentCollection<T>
    implements Map<String, T>
{
    private Map<String, T> components;

    private Map<String, T> customAdditions = new LinkedHashMap<String, T>();

    public ComponentMap( MutablePlexusContainer container, Class<T> type, String role, List<String> roleHints, String hostComponent )
    {
        super( container, type, role, roleHints, hostComponent );
    }

    public int size()
    {
        return getComponentDescriptorMap().size();
    }

    public boolean isEmpty()
    {
        return getComponentDescriptorMap().isEmpty();
    }

    public boolean containsKey( Object key )
    {
        return getComponentDescriptorMap().containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return getMap().containsValue( value );
    }

    public T get( Object k )
    {
        return getMap().get( k );
    }

    public synchronized T put( String key, T value )
    {
        logger.warn( "Custom "
                     + role
                     + " implementations should NOT be added directly to this Map. Instead, add them as Plexus components." );

        T prev = customAdditions.put( key, value );
        if ( prev == null )
        {
            prev = getComponentMap().get( key );
        }

        return prev;
    }

    public synchronized void putAll( Map<? extends String, ? extends T> map )
    {
        logger.warn( "Custom "
                     + role
                     + " implementations should NOT be added directly to this Map. Instead, add them as Plexus components." );

        customAdditions.putAll( map );
    }

    public Set<String> keySet()
    {
        return getMap().keySet();
    }

    public Collection<T> values()
    {
        return getMap().values();
    }

    public Set<Map.Entry<String, T>> entrySet()
    {
        return getMap().entrySet();
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Map ) )
        {
            return false;
        }

        Map<?,?> object = (Map<?,?>) o;
        return getMap().equals( object );
    }

    public int hashCode()
    {
        return getMap().hashCode();
    }

    public synchronized T remove( Object key )
    {
        logger.warn( "Items in this Map should NOT be removed directly. If the matching entry is a component, it will NOT be removed." );

        if ( key instanceof String )
        {
            if ( customAdditions.containsKey( key ) )
            {
                return customAdditions.remove( key );
            }
        }

        return null;
    }

    private synchronized Map<String, T> getMap()
    {
        Map<String, T> result = getComponentMap();

        if ( !customAdditions.isEmpty() )
        {
            result.putAll( customAdditions );
        }

        return result;
    }

    private synchronized Map<String, T> getComponentMap()
    {
        if ( ( components == null ) || checkUpdate() )
        {
            Map<String, T> componentMap = new LinkedHashMap<String, T>();

            Map<String, ComponentDescriptor<T>> descriptorMap = getComponentDescriptorMap();

            if ( roleHints != null )
            {
                // we must follow the order given in roleHints
                for ( String roleHint : roleHints )
                {
                    ComponentDescriptor<T> componentDescriptor = descriptorMap.get( roleHint );

                    T component = lookup( componentDescriptor );

                    if ( component != null )
                    {
                        componentMap.put( roleHint, component );
                    }
                }
            }
            else
            {
                for ( Entry<String, ComponentDescriptor<T>> entry : descriptorMap.entrySet() )
                {
                    String roleHint = entry.getKey();

                    ComponentDescriptor<T> componentDescriptor = entry.getValue();

                    T component = lookup( componentDescriptor );

                    if ( component != null )
                    {
                        componentMap.put( roleHint, component );
                    }
                }
            }
            components = componentMap;
        }

        return components;
    }

    @Override
    protected boolean checkUpdate()
    {
        if ( super.checkUpdate() )
        {
            components = null;

            return true;
        }

        return false;
    }

    protected void releaseAllCallback()
    {
        if ( components != null )
        {
            try
            {
                container.releaseAll( components );
            }
            catch ( ComponentLifecycleException e )
            {
                logger.debug( "Error releasing components in active collection: " + e.getMessage(), e );
            }

            components = null;
        }
    }

}
