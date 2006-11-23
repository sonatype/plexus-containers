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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private List componentDiscoverers;

    private List listeners;

    private Map componentDiscoveryListeners;

    public List getComponentDiscoverers()
    {
        return componentDiscoverers;
    }

    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( componentDiscoveryListeners == null )
        {
            componentDiscoveryListeners = new LinkedHashMap();
        }

        //TODO: want to know the listener by id, but this creates an API incompatibility with maven 2.0.4

        //if ( !componentDiscoveryListeners.containsKey( listener.getId() ) )
        if ( !componentDiscoveryListeners.containsKey( listener ) )
        {
            componentDiscoveryListeners.put( listener, listener );
            //componentDiscoveryListeners.put( listener.getId(), listener );
        }
    }

    public void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( componentDiscoveryListeners != null )
        {
            componentDiscoveryListeners.remove( listener );
        }
    }

    public void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event )
    {
        if ( componentDiscoveryListeners != null )
        {
            for ( Iterator i = componentDiscoveryListeners.values().iterator(); i.hasNext(); )
            {
                ComponentDiscoveryListener listener = (ComponentDiscoveryListener) i.next();

                listener.componentDiscovered( event );
            }
        }
    }

    public List getListeners()
    {
        return listeners;
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
