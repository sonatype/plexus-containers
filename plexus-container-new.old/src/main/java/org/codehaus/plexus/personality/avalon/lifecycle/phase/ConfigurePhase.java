package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Configuration configuration = manager.getComponentDescriptor().getConfiguration();

        if ( object instanceof Configurable )
        {
            if ( null == configuration )
            {
                configuration = DefaultConfiguration.EMPTY_CONFIGURATION;
            }
            ( (Configurable) object ).configure( configuration );
        }
    }
}
