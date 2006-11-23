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
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl
 */
public class ComponentMap
    extends AbstractComponentCollection
    implements Map
{
    /**
     * The Map of components we are holding keyed by role hint.
     */
    private Map components;

    public ComponentMap( String role, Map components )
    {
        super( role );

        this.components = components;
    }

    public int size()
    {
        return components.size();
    }

    public boolean isEmpty()
    {
        return components.isEmpty();
    }

    public boolean containsKey( Object key )
    {
        return components.containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return components.containsValue( value );
    }

    public Object get( Object key )
    {
        return components.get( key );
    }

    public Object put( Object key, Object value )
    {
        return components.put( key, value );
    }

    public Object remove( Object object )
    {
        return components.remove( object );
    }


    public void putAll( Map map )
    {
        components.putAll( map );
    }

    public void clear()
    {
        components.clear();
    }

    public Set keySet()
    {
        return components.keySet();
    }

    public Collection values()
    {
        return components.values();
    }

    public Set entrySet()
    {
        return components.entrySet();
    }

    public boolean equals( Object object )
    {
        return components.equals( object );
    }

    public int hashCode()
    {
        return components.hashCode();
    }
}
