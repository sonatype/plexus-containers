package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LogDisablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager componentManager )
        throws PhaseExecutionException
    {
        LoggerManager loggerManager;
        ComponentDescriptor descriptor;

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
            loggerManager.returnComponentLogger( descriptor.getRole(), descriptor.getRoleHint() );
        }
    }
}
