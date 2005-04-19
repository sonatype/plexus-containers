package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class InitializePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Initializable )
        {
            try
            {
                ( (Initializable) object ).initialize();
            }
            catch ( InitializationException e )
            {
                throw new PhaseExecutionException( "Error initialising component", e );
            }
        }
    }
}
