package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.plexus.PlexusLifecycleHandler;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        ComponentConfigurator componentConfigurator =
            (ComponentConfigurator) manager.getLifecycleHandler().getEntities().get( PlexusLifecycleHandler.COMPONENT_CONFIGURATOR );

        if ( manager.getComponentDescriptor().hasConfiguration() )
        {
            componentConfigurator.configureComponent( object, manager.getComponentDescriptor().getConfiguration() );
        }
    }
}
