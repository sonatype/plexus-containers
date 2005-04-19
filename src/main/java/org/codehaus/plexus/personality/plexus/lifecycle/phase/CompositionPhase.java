package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
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
        throws PhaseExecutionException
    {
        // We only need to assemble a component if it specifies requirements.

        PlexusContainer container = manager.getContainer();

        ComponentDescriptor descriptor = manager.getComponentDescriptor();

        try
        {
            container.composeComponent( object, descriptor );
        }
        catch ( CompositionException e )
        {
            throw new PhaseExecutionException( "Error composing component", e );
        }
        catch ( UndefinedComponentComposerException e )
        {
            throw new PhaseExecutionException( "Error composing component", e );
        }
    }
}
