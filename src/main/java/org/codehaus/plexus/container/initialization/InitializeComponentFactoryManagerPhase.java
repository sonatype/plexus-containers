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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author Jason van Zyl
 */
public class InitializeComponentFactoryManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration configuration = context.getContainerConfiguration();

        PlexusConfiguration c = configuration.getChild( "component-factory-manager" );

        setupCoreComponent( "component-factory-manager", configurator, c, context.getContainer() );

        if ( context.getContainer().getComponentFactoryManager() instanceof Contextualizable )
        {
            context.getContainer().getContext().put( PlexusConstants.PLEXUS_KEY, context.getContainer() );

            try
            {
                ( (Contextualizable) context.getContainer().getComponentFactoryManager() ).contextualize( context.getContainer().getContext() );
            }
            catch ( ContextException e )
            {
                throw new ContainerInitializationException( "Error contextualization component factory manager.", e );
            }
        }
    }
}
