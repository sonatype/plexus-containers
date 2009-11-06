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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.source.ChainedConfigurationSource;
import org.codehaus.plexus.configuration.source.ConfigurationSource;

/**
 * @author Jason van Zyl
 * @author cstamas
 */
public class InitializeUserConfigurationSourcePhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        ConfigurationSource existingConfigurationSource = context.getContainer().getConfigurationSource();

        try
        {
            // is the user overriding the ConfigurationSource (role-hint: default) or only extending it?
            if ( context.getContainer().hasComponent( ConfigurationSource.class, PlexusConstants.PLEXUS_DEFAULT_HINT ) )
            {
                // overriding

                ConfigurationSource cs = context.getContainer().lookup( ConfigurationSource.class );

                // swap the user provided configuration source with current one
                context.getContainer().setConfigurationSource( cs );
            }
            else
            {
                // extending
                List<ConfigurationSource> userConfigurationSources = context.getContainer().lookupList( ConfigurationSource.class );

                if ( userConfigurationSources.size() > 0 )
                {
                    List<ConfigurationSource> configurationSources =
                        new ArrayList<ConfigurationSource>( userConfigurationSources.size() + 1 );

                    // adding user provied ones to be able to interfere
                    configurationSources.addAll( userConfigurationSources );

                    // at the end adding the container source, to make sure config will be returned
                    // from plexus.xml if no user interference is given
                    configurationSources.add( existingConfigurationSource );

                    ConfigurationSource configurationSource = new ChainedConfigurationSource( configurationSources );

                    context.getContainer().setConfigurationSource( configurationSource );

                }

                // register the default source, either the chained or the existing one as default
                ComponentDescriptor<ConfigurationSource> cd = new ComponentDescriptor<ConfigurationSource>();

                cd.setRole( ConfigurationSource.ROLE );

                cd.setRoleHint( PlexusConstants.PLEXUS_DEFAULT_HINT );

                cd.setImplementationClass( context.getContainer().getConfigurationSource().getClass() );

                try
                {
                    context.getContainer().addComponentDescriptor( cd );
                }
                catch ( CycleDetectedInComponentGraphException cre )
                {
                    throw new ContainerInitializationException( "Error setting up configuration source.", cre );
                }
            }

        }
        catch ( ComponentLookupException e )
        {
            throw new ContainerInitializationException( "Error setting up user configuration source.", e );
        }
    }
}
