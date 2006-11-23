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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class StartLoadOnStartComponentsPhase
    extends AbstractContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration[] loadOnStartComponents =
            context.getContainer().getConfiguration().getChild( "load-on-start" ).getChildren( "component" );

        context.getContainer().getLogger().debug(
            "Found " + loadOnStartComponents.length + " components to load on start" );

        for ( int i = 0; i < loadOnStartComponents.length; i++ )
        {
            String role = loadOnStartComponents[i].getChild( "role" ).getValue( null );

            String roleHint = loadOnStartComponents[i].getChild( "role-hint" ).getValue( null );

            if ( role == null )
            {
                throw new ContainerInitializationException( "Missing 'role' element from load-on-start." );
            }

            try
            {
                if ( roleHint == null )
                {
                    context.getContainer().getLogger().info( "Loading on start [role]: " + "[" + role + "]" );

                    context.getContainer().lookup( role );
                }
                else if ( roleHint.equals( "*" ) )
                {
                    context.getContainer().getLogger().info(
                        "Loading on start all components with [role]: " + "[" + role + "]" );

                    context.getContainer().lookupList( role );
                }
                else
                {
                    context.getContainer().getLogger().info(
                        "Loading on start [role,roleHint]: " + "[" + role + "," + roleHint + "]" );

                    context.getContainer().lookup( role, roleHint );
                }
            }
            catch ( ComponentLookupException e )
            {
                throw new ContainerInitializationException( "Error looking up load-on-start component.", e );
            }
        }
    }
}
