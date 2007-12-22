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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl FIXME: [jdcasey] We need to review the efficiency (in speed and memory) of this collection...
 */
public class ComponentList
    extends AbstractComponentCollection
    implements List
{
    private List components;

    public ComponentList( PlexusContainer container, ClassRealm realm, String role, List roleHints, String hostComponent )
    {
        super( container, realm, role, roleHints, hostComponent );
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

    public Iterator iterator()
    {
        return getList().iterator();
    }

    public Object[] toArray()
    {
        return getList().toArray();
    }

    public Object[] toArray( Object[] ts )
    {
        return getList().toArray( ts );
    }

    public boolean add( Object object )
    {
        if ( components == null )
        {
            components = new ArrayList();
        }

        components.add( object );

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

    public boolean containsAll( Collection collection )
    {
        return getList().containsAll( collection );
    }

    public boolean addAll( Collection collection )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public boolean addAll( int i, Collection collection )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public boolean removeAll( Collection collection )
    {
        return getList().removeAll( collection );
    }

    public boolean retainAll( Collection collection )
    {
        return getList().retainAll( collection );
    }

    public boolean equals( Object object )
    {
        return getList().equals( object );
    }

    public int hashCode()
    {
        return getList().hashCode();
    }

    public Object get( int i )
    {
        return getList().get( i );
    }

    public Object set( int i, Object object )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public void add( int i, Object object )
    {
        throw new UnsupportedOperationException( "You cannot modify this list. This list is a requirement of "
            + hostComponent + " and managed by the container." );
    }

    public Object remove( int i )
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

    public ListIterator listIterator()
    {
        return getList().listIterator();
    }

    public ListIterator listIterator( int i )
    {
        return getList().listIterator( i );
    }

    public List subList( int i, int i1 )
    {
        return getList().subList( i, i1 );
    }

    private List getList()
    {
        // NOTE: If we cache the component map, we have a problem with releasing any of the
        // components in this map...we need to be able to release them all.
        if ( ( components == null ) || checkUpdate() )
        {
            List componentList = new ArrayList();

            Map descriptorMap = getComponentDescriptorMap();
            Map lookupRealms = getLookupRealmMap();

            if ( roleHints != null )
            {
                // we must follow the order in roleHints
                for ( Iterator hints = roleHints.iterator(); hints.hasNext(); )
                {
                    String roleHint = (String) hints.next();

                    ComponentDescriptor cd = (ComponentDescriptor) descriptorMap.get( roleHint );
                    ClassRealm realm = (ClassRealm) lookupRealms.get( cd.getRealmId() );

                    Object component = lookup( role, roleHint, realm );
                    if ( component != null )
                    {
                        componentList.add( component );
                    }
                }
            }
            else
            {
                for ( Iterator it = descriptorMap.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    String roleHint = (String) entry.getKey();
                    String realmId = ( (ComponentDescriptor) entry.getValue() ).getRealmId();

                    ClassRealm realm = (ClassRealm) lookupRealms.get( realmId );

                    Object component = lookup( role, roleHint, realm );
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

            components.clear();
            components = null;
        }
    }
}
