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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.source.ChainedConfigurationSource;
import org.codehaus.plexus.configuration.source.ConfigurationSource;
import org.codehaus.plexus.configuration.source.ContainerConfigurationSource;

/** @author Jason van Zyl */
public class InitializeConfigurationSourcePhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        ConfigurationSource configurationSource = context.getContainer().getConfigurationSource();

        // ----------------------------------------------------------------------
        // The configurationSource may have been set programmatically so we need
        // to check. If it hasn't then we will try to look up a configurationSource
        // that may have been specified in the plexus.xml file. If that doesn't
        // work then we'll programmatcially stuff in the console logger.
        // ----------------------------------------------------------------------

        if ( configurationSource == null )
        {

            // adding default source for container to enable lookups
            context.getContainer().setConfigurationSource( new ContainerConfigurationSource() );

            try
            {
                // is the user overriding the ConfigurationSource (role-hint: default) or only extending it?
                if ( context
                    .getContainer().hasComponent( ConfigurationSource.ROLE, PlexusConstants.PLEXUS_DEFAULT_HINT ) )
                {
                    // overriding

                    ConfigurationSource cs = (ConfigurationSource) context.getContainer().lookup(
                        ConfigurationSource.ROLE,
                        PlexusConstants.PLEXUS_DEFAULT_HINT );

                    // swap the user provided configuration source with current one

                    context.getContainer().setConfigurationSource( cs );
                }
                else
                {
                    // extending
                    
                    List userConfigurationSources = context.getContainer().lookupList( ConfigurationSource.class );

                    if ( userConfigurationSources.size() > 0 )
                    {
                        List configurationSources = new ArrayList( userConfigurationSources.size() + 1 );

                        configurationSources.add( new ContainerConfigurationSource() );

                        configurationSources.addAll( userConfigurationSources );

                        configurationSource = new ChainedConfigurationSource( configurationSources );

                        context.getContainer().setConfigurationSource( configurationSource );
                    }
                }

            }
            catch ( ComponentLookupException e )
            {
                throw new ContainerInitializationException( "Error setting up user configuration source.", e );
            }

            ComponentDescriptor cd = new ComponentDescriptor();

            cd.setRole( ConfigurationSource.ROLE );

            cd.setRoleHint( PlexusConstants.PLEXUS_DEFAULT_HINT );

            cd.setImplementation( context.getContainer().getConfigurationSource().getClass().getName() );

            try
            {
                context.getContainer().addComponentDescriptor( cd );
            }
            catch ( ComponentRepositoryException cre )
            {
                throw new ContainerInitializationException( "Error setting up configuration source.", cre );
            }

        }
    }
}
