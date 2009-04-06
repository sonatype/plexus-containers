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
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private final List<ComponentDiscoverer> componentDiscoverers = new ArrayList<ComponentDiscoverer>();

    // todo dain change this to LinkedHashSet<ComponentDiscoveryListener> (requires change to maven)
    private final Map<ComponentDiscoveryListener, Object> listeners = new LinkedHashMap<ComponentDiscoveryListener, Object>();

    public synchronized void addComponentDiscoverer( ComponentDiscoverer discoverer )
    {
        componentDiscoverers.add( discoverer );
    }

    // todo this is not thread safe... we are returning the raw collection
    public synchronized List<ComponentDiscoverer> getComponentDiscoverers()
    {
        return componentDiscoverers;
    }

    // Listeners

    // todo this is not thread safe... we are returning the raw collection
    public synchronized Map<ComponentDiscoveryListener, Object> getComponentDiscoveryListeners()
    {
        return listeners;
    }

    public synchronized void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( !listeners.containsKey( listener ) )
        {
            listeners.put( listener, new Object() );
        }
    }

    public synchronized void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        listeners.remove( listener );
    }

    public void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event )
    {
        Set<ComponentDiscoveryListener> listeners;
        synchronized ( this )
        {
            listeners = this.listeners.keySet();
        }

        for ( ComponentDiscoveryListener listener : listeners )
        {
            listener.componentDiscovered( event );
        }
    }
}
