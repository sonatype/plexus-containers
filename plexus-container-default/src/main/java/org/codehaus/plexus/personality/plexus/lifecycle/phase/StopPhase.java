package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class StopPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Startable )
        {
            try
            {
                ( (Startable) object ).stop();
            }
            catch ( StoppingException e )
            {
                throw new PhaseExecutionException( "Error stopping component", e );
            }
        }
    }
}
