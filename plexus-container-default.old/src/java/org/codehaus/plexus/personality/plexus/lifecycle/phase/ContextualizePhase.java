package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Contextualizable )
        {
            Context context = manager.getContainer().getContext();

            ( (Contextualizable) object ).contextualize( context );
        }
    }
}
