package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.MutablePlexusContainer;
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
        List listeners = context.getContainer().getComponentDiscovererManager().getListeners();

        if ( listeners != null )
        {
            for ( Iterator i = listeners.iterator(); i.hasNext(); )
            {
                DiscoveryListenerDescriptor listenerDescriptor = (DiscoveryListenerDescriptor) i.next();

                String role = listenerDescriptor.getRole();

                System.out.println( "containerContext.getContainer() = " + context.getContainer() );

                System.out.println( "containerContext.getContainer().getName() = " + context.getContainer().getName() );

                System.out.println( "role = " + role );

                try
                {
                    MutablePlexusContainer container = context.getContainer();

                    ComponentDiscoveryListener listener;

                    if ( container.getParentContainer() != null )
                    {
                        listener = (ComponentDiscoveryListener) container.getParentContainer().lookup( role );

                        System.out.println( "listener from parent = " + listener );

                        if ( listener == null )
                        {
                            listener = (ComponentDiscoveryListener) container.lookup( role );
                        }
                    }
                    else
                    {
                        listener = (ComponentDiscoveryListener) container.lookup( role );
                    }

                    System.out.println( "listener this container = " + listener );


                    container.getComponentDiscovererManager().registerComponentDiscoveryListener( listener );
                }
                catch ( ComponentLookupException e )
                {
                    throw new ContainerInitializationException( "Error looking up component discovery listener.", e );
                }
            }
        }
    }
}
