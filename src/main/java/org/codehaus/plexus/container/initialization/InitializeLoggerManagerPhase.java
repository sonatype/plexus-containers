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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author Jason van Zyl
 */
public class InitializeLoggerManagerPhase
    extends AbstractCoreComponentInitializationPhase
{
    public void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        LoggerManager loggerManager = context.getContainer().getLoggerManager();

        // ----------------------------------------------------------------------
        // The logger manager may have been set programmatically so we need
        // to check. If it hasn't
        // ----------------------------------------------------------------------

        if ( loggerManager == null )
        {
            try
            {
                loggerManager = (LoggerManager) context.getContainer().lookup( LoggerManager.ROLE );

                context.getContainer().setLoggerManager( loggerManager );
            }
            catch ( ComponentLookupException e )
            {
                throw new ContainerInitializationException( "Unable to locate logger manager", e );
            }
        }

        //TODO: the container should allow this logger manager change, don't use the DefaultPlexusContainer 
        context.getContainer().enableLogging( loggerManager.getLoggerForComponent( PlexusContainer.class.getName() ) );
    }

}
