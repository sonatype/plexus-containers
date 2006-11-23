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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jason van Zyl
 */
public class ComponentDiscoveryPhase
    extends AbstractContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        try
        {
            discoverComponents( context.getContainer() );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ContainerInitializationException( "Error discovering components.", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new ContainerInitializationException( "Error discovering components.", e );
        }
    }

    public static List discoverComponents( DefaultPlexusContainer container )
        throws PlexusConfigurationException, ComponentRepositoryException
    {
        // We are assuming that any component which is designated as a component discovery
        // listener is listed in the plexus.xml file that will be discovered and processed
        // before the components.xml are discovered in JARs and processed.

        List discoveredComponentDescriptors = new ArrayList();

        for ( Iterator i = container.getComponentDiscovererManager().getComponentDiscoverers().iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            List componentSetDescriptors = componentDiscoverer.findComponents( container.getContext(), container.getContainerRealm() );

            for ( Iterator j = componentSetDescriptors.iterator(); j.hasNext(); )
            {
                ComponentSetDescriptor componentSet = (ComponentSetDescriptor) j.next();

                List componentDescriptors = componentSet.getComponents();

                if ( componentDescriptors != null )
                {
                    for ( Iterator k = componentDescriptors.iterator(); k.hasNext(); )
                    {
                        ComponentDescriptor componentDescriptor = (ComponentDescriptor) k.next();

                        componentDescriptor.setComponentSetDescriptor( componentSet );

                        // If the user has already defined a component descriptor for this particular
                        // component then do not let the discovered component descriptor override
                        // the user defined one.
                        if ( container.getComponentDescriptor( componentDescriptor.getComponentKey() ) == null )
                        {
                            container.addComponentDescriptor( componentDescriptor );

                            // We only want to add components that have not yet been
                            // discovered in a parent realm. We don't quite have fine
                            // grained control over this right now but this is for
                            // dynamic additions which are only happening from maven
                            // at the moment. And plugins have a parent realm and
                            // a grand parent realm so if the component has been
                            // discovered it's most likely in those realms.

                            // I actually need to keep track of what realm a component
                            // was discovered in so that i can accurately search the
                            // parents.

                            discoveredComponentDescriptors.add( componentDescriptor );
                        }
                    }

                    //discoveredComponentDescriptors.addAll( componentDescriptors );
                }
            }
        }

        return discoveredComponentDescriptors;
    }
}
