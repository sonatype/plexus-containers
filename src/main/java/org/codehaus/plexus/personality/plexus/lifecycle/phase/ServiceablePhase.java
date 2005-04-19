package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ServiceablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
    {
        if ( object instanceof Serviceable )
        {
            PlexusContainer container = manager.getContainer();

            ( (Serviceable) object ).service( new PlexusContainerLocator(container) );
        }
    }
}
