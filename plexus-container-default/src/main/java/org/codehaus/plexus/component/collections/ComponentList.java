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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Jason van Zyl
 */
public class ComponentList
    extends AbstractComponentCollection
    implements List
{
    private List components;

    public ComponentList( PlexusContainer container,
                          ClassRealm realm,
                          String role,
                          List roleHints,
                          String hostComponent
    )
    {
        super( container, realm, role, roleHints, hostComponent );
    }

    public int size()
    {
        return getList().size() ;
    }

    public boolean isEmpty()
    {
        return getList().isEmpty() ;
    }

    public boolean contains( Object object )
    {
        return getList().contains( object ) ;
    }

    public Iterator iterator()
    {
        return getList().iterator() ;
    }

    public Object[] toArray()
    {
        return getList().toArray() ;
    }

    public Object[] toArray( Object[] ts )
    {
        return getList().toArray( ts ) ;
    }

    public boolean add( Object object )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public boolean remove( Object object )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public boolean containsAll( Collection collection )
    {
        return getList().containsAll( collection ) ;
    }

    public boolean addAll( Collection collection )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public boolean addAll( int i, Collection collection )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public boolean removeAll( Collection collection )
    {
        return getList().removeAll( collection ) ;
    }

    public boolean retainAll( Collection collection )
    {
        return getList().retainAll( collection ) ;
    }

    public void clear()
    {
        getList().clear() ;
    }

    public boolean equals( Object object )
    {
        return getList().equals( object ) ;
    }

    public int hashCode()
    {
        return getList().hashCode() ;
    }

    public Object get( int i )
    {
        return getList().get( i ) ;
    }

    public Object set( int i, Object object )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public void add( int i, Object object )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public Object remove( int i )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this list. This list is a requirement of " + hostComponent + " and managed by the container." );
    }

    public int indexOf( Object object )
    {
        return getList().indexOf( object ) ;
    }

    public int lastIndexOf( Object object )
    {
        return getList().lastIndexOf( object ) ;
    }

    public ListIterator listIterator()
    {
        return getList().listIterator() ;
    }

    public ListIterator listIterator( int i )
    {
        return getList().listIterator( i ) ;
    }

    public List subList( int i, int i1 )
    {
        return getList().subList( i, i1 ) ;
    }

    private List getList()
    {
        if ( ( components == null ) || requiresUpdate() )
        {
            Set c = new LinkedHashSet();

            for ( Iterator it = getLookupRealms().iterator(); it.hasNext(); )
            {
                ClassRealm r = (ClassRealm) it.next();

                try
                {
                    c.addAll( container.lookupList( role, roleHints, r ) );
                }
                catch ( ComponentLookupException e )
                {
                    logger.debug( "Failed to lookup list for role: "
                                  + role
                                  + "(hints: "
                                  + ( roleHints == null ? "-none-"
                                                  : StringUtils.join( roleHints.iterator(), ", " ) )
                                  + ") in realm:\n" + realm, e );
                }
            }

            components = c.isEmpty() ? Collections.EMPTY_LIST : new ArrayList( c );
        }

        return components;
    }
}
