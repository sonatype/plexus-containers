package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class ReconfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Configuration configuration = (Configuration) manager.getLifecycleHandler().getEntities().get( "configuration" );

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
