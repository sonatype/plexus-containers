package org.codehaus.plexus.container.initialization;

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
