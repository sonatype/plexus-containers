package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.component.manager.ComponentManager;
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

        ComponentComposer componentComposer = (ComponentComposer) manager.getContainer().lookup( ComponentComposer.ROLE );

        componentComposer.assembleComponent( object, manager.getComponentDescriptor(), manager.getContainer() );
    }
}
