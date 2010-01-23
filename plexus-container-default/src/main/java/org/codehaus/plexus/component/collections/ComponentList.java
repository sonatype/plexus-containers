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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jason van Zyl FIXME: [jdcasey] We need to review the efficiency (in speed and memory) of this collection...
 */
public class ComponentList<T>
    extends AbstractComponentCollection<T>
    implements List<T>
{
    private List<T> components;

    public ComponentList( MutablePlexusContainer container, Class<T> type, String role, List<String> roleHints, String hostComponent )
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

    public boolean contains( Object object )
    {
        return getList().contains( object );
    }

    public Iterator<T> iterator()
    {
        return getList().iterator();
    }

    public Object[] toArray()
    {
        return getList().toArray();
    }

    public <X> X[] toArray( X[] ts )
    {
        return getList().toArray( ts );
    }

    public synchronized boolean add( T object )
    {
        getList().add( object );

        /*
         * PLX-352 This is strictly to support the hack in the Ant Run plugin that tries to poke in a custom converter.
         * We need a better way to register converters to plexus and not hit the default converter lookup directly.
         * throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of " +
         * hostComponent + " and managed by the container." );
         */

        return true;
    }

    public boolean remove( Object object )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public boolean containsAll( Collection<?> collection )
    {
        return getList().containsAll( collection );
    }

    public boolean addAll( Collection<? extends T> collection )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public boolean addAll( int i, Collection<? extends T> collection )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public synchronized boolean removeAll( Collection<?> collection )
    {
        return getList().removeAll( collection );
    }

    public synchronized boolean retainAll( Collection<?> collection )
    {
        return getList().retainAll( collection );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof List ) )
        {
            return false;
        }

        List<?> other = (List<?>) o;
        return getList().equals( other );
    }

    public int hashCode()
    {
        return getList().hashCode();
    }

    public T get( int i )
    {
        return getList().get( i );
    }

    public T set( int i, T object )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public void add( int i, T object )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public T remove( int i )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public int indexOf( Object object )
    {
        return getList().indexOf( object );
    }

    public int lastIndexOf( Object object )
    {
        return getList().lastIndexOf( object );
    }

    public ListIterator<T> listIterator()
    {
        return getList().listIterator();
    }

    public ListIterator<T> listIterator( int index )
    {
        return getList().listIterator( index );
    }

    public List<T> subList( int fromIndex, int toIndex )
    {
        return getList().subList( fromIndex, toIndex );
    }

    private synchronized List<T> getList()
    {
        // NOTE: If we cache the component map, we have a problem with releasing any of the
        // components in this map...we need to be able to release them all.
        if ( ( components == null ) || checkUpdate() )
        {
            List<T> componentList = new ArrayList<T>();

            Map<String, ComponentDescriptor<T>> descriptorMap = getComponentDescriptorMap();

            if ( roleHints != null )
            {
                // we must follow the order in roleHints
                for ( String roleHint : roleHints )
                {
                    ComponentDescriptor<T> componentDescriptor = descriptorMap.get( roleHint );

                    T component = lookup( componentDescriptor );

                    if ( component != null )
                    {
                        componentList.add( component );
                    }
                }
            }
            else
            {
                for ( Entry<String, ComponentDescriptor<T>> entry : descriptorMap.entrySet() )
                {
                    ComponentDescriptor<T> componentDescriptor = entry.getValue();

                    T component = lookup( componentDescriptor );

                    if ( component != null )
                    {
                        componentList.add( component );
                    }
                }
            }
            components = componentList;
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
