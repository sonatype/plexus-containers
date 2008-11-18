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
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusComponentDescriptorMerger;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.util.ArrayList;
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
            discoverComponents( context.getContainer(), context.getContainer().getContainerRealm() );
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

    public static List<ComponentDescriptor<?>> discoverComponents( DefaultPlexusContainer container, ClassRealm realm )
        throws PlexusConfigurationException,
            ComponentRepositoryException
    {
        List<ComponentDescriptor<?>> discoveredComponentDescriptors = new ArrayList<ComponentDescriptor<?>>();

        for ( ComponentDiscoverer componentDiscoverer : container.getComponentDiscovererManager().getComponentDiscoverers() )
        {
            for ( ComponentSetDescriptor componentSet : componentDiscoverer.findComponents( container.getContext(), realm ) )
            {
                for ( ComponentDescriptor<?> componentDescriptor : componentSet.getComponents() )
                {
                    container.addComponentDescriptor( componentDescriptor );
                    
                    discoveredComponentDescriptors.add( componentDescriptor );
                }
            }
        }

        return discoveredComponentDescriptors;
    }
}
