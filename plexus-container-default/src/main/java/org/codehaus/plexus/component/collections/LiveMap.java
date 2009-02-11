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

import static com.google.common.base.ReferenceType.STRONG;
import static com.google.common.base.ReferenceType.WEAK;
import com.google.common.collect.ReferenceMap;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptorListener;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class LiveMap<T>
    implements ConcurrentMap<String, T>
{
    /** The reference to the PlexusContainer */
    private final MutablePlexusContainer container;

    /** The type of the components held by this collection */
    private final Class<T> type;

    /** The role hint of the components we are holding in this Collection. */
    private final List<String> roleHints;

    /** The component that requires this collection of components */
    private final String hostComponent;

    private final ConcurrentMap<String, T> components = new ReferenceMap<String, T>( STRONG, WEAK );
    private final Map<String, T> immutableComponents = unmodifiableMap( components );
    private final ConcurrentMap<ComponentDescriptor<? extends T>, T> componentsByDescriptor =
        new ReferenceMap<ComponentDescriptor<? extends T>, T>( STRONG, WEAK );

    public LiveMap( MutablePlexusContainer container, Class<T> type, List<String> roleHints, String hostComponent )
    {
        this.container = container;

        this.type = type;

        if ( roleHints == null )
        {
            this.roleHints = null;
        }
        else
        {
            this.roleHints = unmodifiableList( new ArrayList<String>( roleHints ) );
        }

        this.hostComponent = hostComponent;

        container.addComponentDescriptorListener( new LiveMapDescriptorListener() );
    }

    public String getHostComponent()
    {
        return hostComponent;
    }

    public boolean isEmpty()
    {
        return immutableComponents.isEmpty();
    }

    public int size()
    {
        return immutableComponents.size();
    }

    public boolean containsKey( Object key )
    {
        return immutableComponents.containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return immutableComponents.containsValue( value );
    }

    public T get( Object key )
    {
        return immutableComponents.get( key );
    }

    public Set<String> keySet()
    {
        return immutableComponents.keySet();
    }

    public Collection<T> values()
    {
        return immutableComponents.values();
    }

    public Set<Entry<String, T>> entrySet()
    {
        return immutableComponents.entrySet();
    }

    public boolean equals( Object o )
    {
        return this == o || ( o instanceof Map && immutableComponents.equals( o ) );
    }

    public int hashCode()
    {
        return immutableComponents.hashCode();
    }

    public String toString()
    {
        return immutableComponents.toString();
    }

    //
    // Unsupported Operations -- Map is immutable
    //

    public T put( String key, T value )
    {
        throw new UnsupportedOperationException();
    }

    public T putIfAbsent( String key, T value )
    {
        throw new UnsupportedOperationException();
    }

    public void putAll( Map<? extends String, ? extends T> t )
    {
        throw new UnsupportedOperationException();
    }

    public T remove( Object k )
    {
        throw new UnsupportedOperationException();
    }

    public boolean remove( Object key, Object value )
    {
        throw new UnsupportedOperationException();
    }

    public boolean replace( String key, T oldValue, T newValue )
    {
        throw new UnsupportedOperationException();
    }

    public T replace( String key, T value )
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    private class LiveMapDescriptorListener implements ComponentDescriptorListener<T>
    {
        public Class<T> getType()
        {
            return type;
        }

        public List<String> getRoleHints()
        {
            return roleHints;
        }

        public synchronized void componentDescriptorAdded( ComponentDescriptor<? extends T> descriptor )
        {
            String roleHint = descriptor.getRoleHint();
            if ( !components.containsKey( roleHint ) && ( roleHints == null || roleHints.contains( roleHint ) ) )
            {
                T component = null;
                try
                {
                    component = container.lookup( descriptor );
                }
                catch ( ComponentLookupException ignored )
                {
                    // ignored - component can't be created which
                }
                components.put( roleHint, component );
                componentsByDescriptor.put( descriptor, component );
            }
        }

        public synchronized void componentDescriptorRemoved( ComponentDescriptor<? extends T> descriptor )
        {
            T component = componentsByDescriptor.remove( descriptor );
            if ( component != null )
            {
                components.remove( descriptor.getRoleHint(), component );
            }
        }
    }
}