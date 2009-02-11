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
import static java.util.Collections.unmodifiableSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class LiveList<T>
    implements List<T>
{
    /** The reference to the PlexusContainer */
    private final MutablePlexusContainer container;

    /** The type of the components held by this collection */
    private final Class<T> type;

    /** The role hint of the components we are holding in this Collection. */
    private final Set<String> roleHints;

    /** The component that requires this collection of components */
    private final String hostComponent;

    private List<T> components = new ArrayList<T>();
    private List<String> componentHints = new ArrayList<String>();

    private boolean hasExternalReference;

    private final ConcurrentMap<ComponentDescriptor<? extends T>, T> componentsByDescriptor =
        new ReferenceMap<ComponentDescriptor<? extends T>, T>( STRONG, WEAK );

    public LiveList( MutablePlexusContainer container, Class<T> type, List<String> roleHints, String hostComponent )
    {
        this.container = container;

        this.type = type;

        if ( roleHints == null )
        {
            this.roleHints = null;
        }
        else
        {
            this.roleHints = unmodifiableSet( new LinkedHashSet<String>( roleHints ) );
        }

        this.hostComponent = hostComponent;

        container.addComponentDescriptorListener( new LiveListDescriptorListener() );
    }

    public String getHostComponent()
    {
        return hostComponent;
    }

    public synchronized boolean isEmpty()
    {
        return components.isEmpty();
    }

    public synchronized int size()
    {
        return components.size();
    }

    public synchronized T get( int index )
    {
        return components.get( index );
    }

    public synchronized boolean contains( Object o )
    {
        return components.contains( o );
    }

    public synchronized boolean containsAll( Collection<?> c )
    {
        return components.containsAll( c );
    }

    public synchronized int indexOf( Object o )
    {
        return components.indexOf( o );
    }

    public synchronized int lastIndexOf( Object o )
    {
        return components.lastIndexOf( o );
    }

    public synchronized List<T> subList( int fromIndex, int toIndex )
    {
        hasExternalReference = true;
        return unmodifiableList( components ).subList( fromIndex, toIndex );
    }

    public synchronized Iterator<T> iterator()
    {
        hasExternalReference = true;
        return unmodifiableList( components ).iterator();
    }

    public synchronized ListIterator<T> listIterator()
    {
        hasExternalReference = true;
        return unmodifiableList( components ).listIterator();
    }

    public synchronized ListIterator<T> listIterator( int index )
    {
        hasExternalReference = true;
        return unmodifiableList( components ).listIterator( index );
    }

    public synchronized Object[] toArray()
    {
        return components.toArray();
    }

    public synchronized <T> T[] toArray( T[] a )
    {
        return components.toArray( a );
    }

    public synchronized boolean equals( Object o )
    {
        return this == o || ( o instanceof List && components.equals( o ) );
    }

    public synchronized int hashCode()
    {
        return components.hashCode();
    }

    public synchronized String toString()
    {
        return components.toString();
    }

    //
    // Unsupported Operations -- Map is immutable
    //

    public T set( int index, T element )
    {
        throw new UnsupportedOperationException();
    }

    public boolean add( T o )
    {
        throw new UnsupportedOperationException();
    }

    public void add( int index, T element )
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll( Collection<? extends T> c )
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll( int index, Collection<? extends T> c )
    {
        throw new UnsupportedOperationException();
    }

    public boolean remove( Object k )
    {
        throw new UnsupportedOperationException();
    }

    public T remove( int index )
    {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll( Collection<?> c )
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll( Collection<?> c )
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    private synchronized void componentDescriptorAdded( ComponentDescriptor<? extends T> descriptor )
    {
        String roleHint = descriptor.getRoleHint();
        if ( !componentsByDescriptor.containsKey( descriptor ) && ( roleHints == null || roleHints.contains( roleHint ) ) )
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

            // if there is an external reference to the components, make a copy before modifying
            if ( hasExternalReference )
            {
                components = new ArrayList<T>( components );
                hasExternalReference = false;
            }

            insertSorted( component, roleHint );
            componentsByDescriptor.put( descriptor, component );
        }
    }

    private synchronized void insertSorted( T targetComponent, String targetHint )
    {
        // if we are not sorting, simply add the component to the end
        if ( roleHints == null )
        {
            components.add( targetComponent );
            return;
        }

        //
        // This algorithm uses iterators, hintOrder over the required sorted order of the components (specified
        // during consturction of LiveList, and componentIterator over the hints of the actual components in the
        // current LiveList.  The componentIterator is advanced until it points to a hint after the targetHint.
        // Then the targetComponent is inserted before this position in the list.
        //

        Iterator<String> hintOrder = roleHints.iterator();

        // inspect each component role until we find one with a role after the target hint
        for ( ListIterator<String> componentIterator = componentHints.listIterator(); componentIterator.hasNext(); )
        {
            String currentHint = componentIterator.next();

            // advance hint iterator until it points to the current hint
            for ( String nextHint = hintOrder.next(); !nextHint.equals( currentHint ); nextHint = hintOrder.next() )
            {
                if ( nextHint.equals( targetHint ) )
                {
                    // the current component's hint is after our target hint, so insert here
                    components.add( componentIterator.previousIndex(), targetComponent );
                    componentHints.add( componentIterator.previousIndex(), targetHint );

                    // break
                    return;
                }
            }
        }

        // walked through whole list and didn't find a hint after our target hint, so insert at the end
        components.add( targetComponent );
        componentHints.add( targetHint );
    }

    private synchronized void componentDescriptorRemoved( ComponentDescriptor<? extends T> descriptor )
    {
        T component = componentsByDescriptor.remove( descriptor );
        if ( component != null )
        {
            // if there is an external reference to the components, make a copy before modifying
            if ( hasExternalReference )
            {
                components = new ArrayList<T>( components );
                hasExternalReference = false;
            }

            Iterator<String> hintIterator = componentHints.iterator();
            for ( Iterator<T> componentIterator = components.iterator(); componentIterator.hasNext(); )
            {
                T current = componentIterator.next();
                hintIterator.next();

                if ( current == component )
                {
                    componentIterator.remove();
                    hintIterator.remove();
                }
            }
        }
    }

    private class LiveListDescriptorListener implements ComponentDescriptorListener<T>
    {
        public Class<T> getType()
        {
            return type;
        }

        public List<String> getRoleHints()
        {
            if ( roleHints != null )
            {
                return new ArrayList<String>( roleHints );
            }
            else
            {
                return null;
            }
        }

        public void componentDescriptorAdded( ComponentDescriptor<? extends T> descriptor )
        {
            LiveList.this.componentDescriptorAdded( descriptor );
        }

        public void componentDescriptorRemoved( ComponentDescriptor<? extends T> descriptor )
        {
            LiveList.this.componentDescriptorRemoved( descriptor );
        }
    }
}