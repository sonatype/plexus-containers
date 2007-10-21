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
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author Jason van Zyl */
public class ComponentMap
    extends AbstractComponentCollection
    implements Map
{
    public ComponentMap( PlexusContainer container,
                         String role,
                         List roleHints )
    {
        super( container, role, roleHints );
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
        return getMap().put( key, value );
    }

    public Object remove( Object object )
    {
        return getMap().remove( object );
    }


    public void putAll( Map map )
    {
        getMap().putAll( map );
    }

    public void clear()
    {
        getMap().clear();
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

    private Map getMap()
    {
        try
        {
            return container.lookupMap( role, roleHints );
        }
        catch ( ComponentLookupException e )
        {
            return Collections.EMPTY_MAP;
        }
    }
}
