package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        Configuration configuration = housing.getComponentManager().getComponentDescriptor().getConfiguration();

        if ( object instanceof Configurable )
        {
            if ( null == configuration )
            {
                final String message = "configuration is null";
                throw new IllegalArgumentException( message );
            }
            ( (Configurable) object ).configure( configuration );
        }
    }
}
