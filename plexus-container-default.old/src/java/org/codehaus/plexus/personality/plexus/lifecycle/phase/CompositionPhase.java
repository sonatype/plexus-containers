package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

/**
 * @todo this little example works but is indicative of of some decoupling that
 * needs to happen wrt the lifecycle handlers. We should be able to specify by
 * configuration which entities for a lifecycle handler are required.
 */
public class CompositionPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        // We only need to assemble a component if it specifies requirements.

        ComponentComposer componentComposer =
            (ComponentComposer) manager.getLifecycleHandler().getEntities().get( "componentComposer" );

        Context context = (Context) manager.getLifecycleHandler().getEntities().get( LifecycleHandler.CONTEXT );

        PlexusContainer container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        ComponentRepository componentRepository =
            (ComponentRepository) manager.getLifecycleHandler().getEntities().get( LifecycleHandler.COMPONENT_REPOSITORY );

        componentComposer.assembleComponent( object, manager.getComponentDescriptor(), container, componentRepository );
    }
}
