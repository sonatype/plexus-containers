package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.DiscoveryListenerDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jason van Zyl
 */
public class RegisterComponentDiscoveryListenersPhase
    extends AbstractContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        List listeners = context.getContainer().getComponentDiscovererManager().getListenerDescriptors();

        if ( listeners != null )
        {
            for ( Iterator i = listeners.iterator(); i.hasNext(); )
            {
                DiscoveryListenerDescriptor listenerDescriptor = (DiscoveryListenerDescriptor) i.next();

                String role = listenerDescriptor.getRole();

                try
                {
                    ComponentDiscoveryListener l = (ComponentDiscoveryListener) context.getContainer().lookup( role );

                    context.getContainer().getComponentDiscovererManager().registerComponentDiscoveryListener( l );
                }
                catch ( ComponentLookupException e )
                {
                    throw new ContainerInitializationException( "Error looking up component discovery listener.", e );
                }
            }
        }
    }
}
