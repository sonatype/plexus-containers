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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

/** @author Jason van Zyl */
public class InitializeLoggerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        LoggerManager loggerManager = context.getContainer().getLoggerManager();

        // ----------------------------------------------------------------------
        // The logger manager may have been set programmatically so we need
        // to check. If it hasn't then we will try to look up a logger manager
        // that may have been specified in the plexus.xml file. If that doesn't
        // work then we'll programmatcially stuff in the console logger.
        // ----------------------------------------------------------------------

        if ( loggerManager == null )
        {
            try
            {
                loggerManager = context.getContainer().lookup( LoggerManager.class );
            }
            catch ( ComponentLookupException e )
            {
                ComponentDescriptor cd = new ComponentDescriptor();

                cd.setRole( LoggerManager.ROLE );

                cd.setRoleHint( PlexusConstants.PLEXUS_DEFAULT_HINT );

                cd.setImplementation( ConsoleLoggerManager.class.getName() );

                try
                {
                    context.getContainer().addComponentDescriptor( cd );
                }
                catch ( CycleDetectedInComponentGraphException cre )
                {
                    throw new ContainerInitializationException( "Error setting up logging manager.", cre );
                }

                loggerManager = new ConsoleLoggerManager( "info" );
            }

            context.getContainer().setLoggerManager( loggerManager );
        }

        Logger logger = loggerManager.getLoggerForComponent( PlexusContainer.class.getName() );

        context.getContainer().enableLogging( logger );
    }
}
