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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author Jason van Zyl */
public class ComponentMap
    extends AbstractComponentCollection
    implements Map
{
    public ComponentMap( PlexusContainer container,
                         ClassRealm realm,
                         String role,
                         List roleHints,
                         String hostComponent )
    {
        super( container, realm, role, roleHints, hostComponent );
    }

    public int size()
    {
        return getMap().size();
    }

    public boolean isEmpty()
    {
        return getMap().isEmpty();
    }

    public boolean containsKey( Object key )
    {
        return getMap().containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return getMap().containsValue( value );
    }

    public Object get( Object key )
    {
        return getMap().get( key );
    }

    public Object put( Object key,
                       Object value )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this map. This map is a requirement of " + hostComponent + " and managed by the container." );
    }

    public void putAll( Map map )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this map. This map is a requirement of " + hostComponent + " and managed by the container." );
    }

    public Set keySet()
    {
        return getMap().keySet();
    }

    public Collection values()
    {
        return getMap().values();
    }

    public Set entrySet()
    {
        return getMap().entrySet();
    }

    public boolean equals( Object object )
    {
        return getMap().equals( object );
    }

    public int hashCode()
    {
        return getMap().hashCode();
    }

    public Object remove( Object object )
    {
        throw new UnsupportedOperationException(
            "You cannot modify this map. This map is a requirement of " + hostComponent + " and managed by the container." );
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    private Map getMap()
    {
        Set realms = getLookupRealms();

        Map components = new LinkedHashMap();

        for ( Iterator it = realms.iterator(); it.hasNext(); )
        {
            ClassRealm r = (ClassRealm) it.next();

            try
            {
//                logger.debug( "Looking up map of components for role: "
//                              + role
//                              + " (hints: "
//                              + ( roleHints == null ? "-none-"
//                                              : StringUtils.join( roleHints.iterator(), ", " ) )
//                              + ") in realm with id: " + r.getId() );

                Map found = container.lookupMap( role, roleHints, r );

//                logger.debug( "Found:\n" + (( found == null ) || found.isEmpty() ? "-none-" : StringUtils.join( found.values().iterator(), "\n" ) ) );

                components.putAll( found );
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

//        logger.debug( "Total components mapped for role: " + role + " --> " + components.size() );

        return components.isEmpty() ? Collections.EMPTY_MAP : components;
    }
}
