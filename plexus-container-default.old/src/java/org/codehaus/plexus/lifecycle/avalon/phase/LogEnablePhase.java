package org.codehaus.plexus.lifecycle.avalon.phase;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class LogEnablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Logger logger = (Logger) manager.getLifecycleHandler().getEntities().get( "logger" );

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
