package org.codehaus.plexus.component.collections;

import java.util.Map;
import java.util.Set;
import java.util.Collection;

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
