package org.codehaus.plexus.component.discovery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private List componentDiscoverers;

    private List componentDiscoveryListeners;

    private List listeners;

    public List getComponentDiscoverers()
    {
        return componentDiscoverers;
    }

    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( componentDiscoveryListeners == null )
        {
            componentDiscoveryListeners = new ArrayList();
        }

        componentDiscoveryListeners.add( listener );
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
            for ( Iterator i = componentDiscoveryListeners.iterator(); i.hasNext(); )
            {
                ComponentDiscoveryListener listener = (ComponentDiscoveryListener) i.next();

                listener.componentDiscovered( event );
            }
        }
    }

    public List getListenerDescriptors()
    {
        return listeners;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        for ( Iterator i = componentDiscoverers.iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            componentDiscoverer.setManager( this );
        }
    }
}
