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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Jason van Zyl
 */
public class ComponentList
    extends AbstractComponentCollection
    implements List
{
    private List components;

    public ComponentList( String role,
                          List components )
    {
        super( role );

        this.components = components;
    }

    public int size()
    {
        return components.size() ;
    }

    public boolean isEmpty()
    {
        return components.isEmpty() ;
    }

    public boolean contains( Object object )
    {
        return components.contains( object ) ;
    }

    public Iterator iterator()
    {
        return components.iterator() ;
    }

    public Object[] toArray()
    {
        return components.toArray() ;
    }

    public Object[] toArray( Object[] ts )
    {
        return components.toArray( ts ) ;
    }

    public boolean add( Object object )
    {
        return components.add( object ) ;
    }

    public boolean remove( Object object )
    {
        return components.isEmpty() ;
    }

    public boolean containsAll( Collection collection )
    {
        return components.containsAll( collection ) ;
    }

    public boolean addAll( Collection collection )
    {
        return components.addAll( collection ) ;
    }

    public boolean addAll( int i, Collection collection )
    {
        return components.addAll( i, collection ) ;
    }

    public boolean removeAll( Collection collection )
    {
        return components.removeAll( collection ) ;
    }

    public boolean retainAll( Collection collection )
    {
        return components.retainAll( collection ) ;
    }

    public void clear()
    {
        components.clear() ;
    }

    public boolean equals( Object object )
    {
        return components.equals( object ) ;
    }

    public int hashCode()
    {
        return components.hashCode() ;
    }

    public Object get( int i )
    {
        return components.get( i ) ;
    }

    public Object set( int i, Object object )
    {
        return components.set( i, object ) ;
    }

    public void add( int i, Object object )
    {
        components.add( i, object ) ;
    }

    public Object remove( int i )
    {
        return components.remove( i ) ;
    }

    public int indexOf( Object object )
    {
        return components.indexOf( object ) ;
    }

    public int lastIndexOf( Object object )
    {
        return components.lastIndexOf( object ) ;
    }

    public ListIterator listIterator()
    {
        return components.listIterator() ;
    }

    public ListIterator listIterator( int i )
    {
        return components.listIterator( i ) ;
    }

    public List subList( int i, int i1 )
    {
        return components.subList( i, i1 ) ;
    }
}
