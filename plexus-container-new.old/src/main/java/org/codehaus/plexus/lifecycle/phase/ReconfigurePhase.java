package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class ReconfigurePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        Configuration configuration = (Configuration) handler.getEntities().get( "configuration" );

        if ( object instanceof Configurable )
        {
            if ( null == configuration )
            {
                final String message = "configuration is null";
                throw new IllegalArgumentException( message );
            }
            ( (Reconfigurable) object ).configure( configuration );
        }
    }
}
