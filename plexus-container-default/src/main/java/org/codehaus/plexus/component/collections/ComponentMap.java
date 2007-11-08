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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl
 * FIXME: [jdcasey] We need to review the efficiency (in speed and memory) of this collection...
 */
public class ComponentMap
    extends AbstractComponentCollection
    implements Map
{
    private Map components;

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
        return getComponentDescriptorMap().size();
    }

    public boolean isEmpty()
    {
        return getComponentDescriptorMap().isEmpty();
    }

    public boolean containsKey( Object key )
    {
        Map descriptorMap = getComponentDescriptorMap();
        return descriptorMap.containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return getMap().containsValue( value );
    }

    public Object get( Object key )
    {
        Map descriptorMap = getComponentDescriptorMap();
        if ( descriptorMap.containsKey( key ) )
        {
            Map lookupRealms = getLookupRealmMap();

            ComponentDescriptor desc = (ComponentDescriptor) descriptorMap.get( key );
            ClassRealm realm = (ClassRealm) lookupRealms.get( desc.getRealmId() );

            return lookup( role, (String) key, realm );
        }

        return null;
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

    private Map getMap()
    {
        if ( ( components == null ) || checkUpdate() )
        {
            components = new LinkedHashMap();

            Map descriptorMap = getComponentDescriptorMap();
            Map lookupRealms = getLookupRealmMap();

            for ( Iterator it = descriptorMap.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();
                String roleHint = (String) entry.getKey();
                String realmId = ( (ComponentDescriptor) entry.getValue() ).getRealmId();

                ClassRealm realm = (ClassRealm) lookupRealms.get( realmId );

                Object component = lookup( role, roleHint, realm );
                if ( component != null )
                {
                    components.put( roleHint, component );
                }
            }
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
