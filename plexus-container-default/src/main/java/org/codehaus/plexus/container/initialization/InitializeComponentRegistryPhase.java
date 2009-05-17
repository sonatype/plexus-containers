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

import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.manager.PerLookupComponentManagerFactory;
import org.codehaus.plexus.component.manager.SingletonComponentManagerFactory;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.DefaultComponentRegistry;
import org.codehaus.plexus.ComponentRegistry;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentRegistryPhase 
    implements ContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        ComponentRepository repository = getComponentRepository( context );

        LifecycleHandlerManager lifecycleHandlerManager = getLifecycleHandlerManager( context );

        ComponentRegistry componentRegistry = new DefaultComponentRegistry( context.getContainer(),
            repository,
            lifecycleHandlerManager );

        componentRegistry.registerComponentManagerFactory( new PerLookupComponentManagerFactory() );

        componentRegistry.registerComponentManagerFactory( new SingletonComponentManagerFactory() );

        context.getContainer().setComponentRegistry( componentRegistry );
    }

    private ComponentRepository getComponentRepository( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        ComponentRepository repository = context.getContainerConfiguration().getComponentRepository();

        // Add the components defined in the container xml configuration
        try
        {
            PlexusConfiguration configuration = context.getContainerXmlConfiguration();

            PlexusConfiguration[] componentConfigurations = configuration.getChild( "components" ).getChildren( "component" );
            for ( PlexusConfiguration componentConfiguration : componentConfigurations )
            {
                ComponentDescriptor<?> componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration, context.getContainer().getContainerRealm() );
                componentDescriptor.setRealm( context.getContainer().getContainerRealm() );
                repository.addComponentDescriptor( componentDescriptor );
            }
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ContainerInitializationException( "Error initializing component repository: " + "Cannot unmarshall component descriptor: ", e );
        }
        catch ( CycleDetectedInComponentGraphException e )
        {
            throw new ContainerInitializationException( "A cycle has been detected in the components of the system: ", e );
        }
        
        return repository;
    }

    private LifecycleHandlerManager getLifecycleHandlerManager( ContainerInitializationContext context )
    {
        LifecycleHandlerManager lifecycleHandlerManager = context.getContainerConfiguration().getLifecycleHandlerManager();
        lifecycleHandlerManager.initialize();
        return lifecycleHandlerManager;
    }
}