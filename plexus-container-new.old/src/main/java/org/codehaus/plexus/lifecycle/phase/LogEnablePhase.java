package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class LogEnablePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        Logger logger = (Logger) handler.getEntities().get( "logger" );

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
