package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LogEnabled;

public class LogEnablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Logger logger = (Logger) manager.getLifecycleHandler().getEntities().get("logger");;

        if ( object instanceof LogEnabled )
        {
            if ( null == logger )
            {
                final String message = "logger is null";

                throw new IllegalArgumentException( message );
            }

            //give a new child logger named by the components class
            ( (LogEnabled) object ).enableLogging( logger.getChildLogger( object.getClass().getName() ) );
        }
    }
}
