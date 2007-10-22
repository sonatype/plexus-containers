package org.codehaus.plexus.component.discovery;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private List componentDiscoverers;

    private Map listeners;

    public void addComponentDiscoverer( ComponentDiscoverer discoverer )
    {
        if ( componentDiscoverers == null )
        {
            componentDiscoverers = new ArrayList();
        }

        componentDiscoverers.add( discoverer );
    }

    public List getComponentDiscoverers()
    {
        return componentDiscoverers;
    }

    // Listeners

    public Map getComponentDiscoveryListeners()
    {
        if ( listeners == null )
        {
            listeners = new LinkedHashMap();
        }

        return listeners;
    }

    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        listeners = getComponentDiscoveryListeners();

        if ( !listeners.containsKey( listener ) )
        {
            listeners.put( listener, listener );
        }
    }

    public void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( listeners != null )
        {
            listeners.remove( listener );
        }
    }

    public void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event )
    {
        if ( listeners != null )
        {
            for ( Iterator i = listeners.values().iterator(); i.hasNext(); )
            {
                ComponentDiscoveryListener listener = (ComponentDiscoveryListener) i.next();

                listener.componentDiscovered( event );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
    {
        for ( Iterator i = componentDiscoverers.iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            componentDiscoverer.setManager( this );
        }
    }
}
