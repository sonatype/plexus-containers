package org.codehaus.plexus.personality.plexus.lifecycle.phase;

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
        ComponentDescriptor descriptor = manager.getComponentDescriptor();

        // We only need to perform assembly if the component has requirements

        if ( descriptor.getRequirements() == null )
        {
            return;
        }

        try
        {
            manager.getContainer().getComponentComposerManager().assembleComponent( object, descriptor, manager.getContainer() );
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
