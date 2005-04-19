package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;

public class LogEnablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager componentManager )
        throws PhaseExecutionException
    {
        LoggerManager loggerManager;
        ComponentDescriptor descriptor;
        Logger logger;

        if ( object instanceof LogEnabled )
        {
            try
            {
                loggerManager = (LoggerManager) componentManager.getContainer().lookup( LoggerManager.ROLE );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "Unable to locate logger manager", e );
            }

            descriptor = componentManager.getComponentDescriptor();
            logger = loggerManager.getLoggerForComponent( descriptor.getRole(), descriptor.getRoleHint() );

            ( (LogEnabled) object ).enableLogging( logger );
        }
    }
}
