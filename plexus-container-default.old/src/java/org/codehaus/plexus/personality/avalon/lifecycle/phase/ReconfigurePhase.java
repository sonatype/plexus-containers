package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ReconfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Configuration configuration = manager.getComponentDescriptor().getConfiguration();

        if ( object instanceof Reconfigurable )
        {
            if ( null == configuration )
            {
                final String message = "configuration is null";
                throw new IllegalArgumentException( message );
            }
            ( (Reconfigurable) object ).reconfigure( configuration );
        }
    }
}
