package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
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
        throws Exception
    {
        LoggerManager loggerManager;
        ComponentDescriptor descriptor;

        if ( object instanceof LogEnabled )
        {
            loggerManager = (LoggerManager) componentManager.getContainer().lookup( LoggerManager.ROLE );
            descriptor = componentManager.getComponentDescriptor();
            loggerManager.returnComponentLogger( descriptor.getRole(), descriptor.getRoleHint() );
        }
    }
}
