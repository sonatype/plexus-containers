package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.composition.ComponentComposer;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.context.Context;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Context context = (Context) manager.getLifecycleHandler().getEntities().get( LifecycleHandler.CONTEXT );

        if ( object instanceof Contextualizable )
        {
            ( (Contextualizable) object ).contextualize( context );
        }
    }
}
