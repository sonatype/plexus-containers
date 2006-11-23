package org.codehaus.plexus.container.initialization;

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

                try
                {
                    MutablePlexusContainer container = context.getContainer();

                    ComponentDiscoveryListener listener;

                    if ( container.getParentContainer() != null )
                    {
                        listener = (ComponentDiscoveryListener) container.getParentContainer().lookup( role );

                        if ( listener == null )
                        {
                            listener = (ComponentDiscoveryListener) container.lookup( role );
                        }
                    }
                    else
                    {
                        listener = (ComponentDiscoveryListener) container.lookup( role );
                    }

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
