package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Context context = (Context) manager.getLifecycleHandler().getEntities().get( "context" );

        if ( object instanceof Contextualizable )
        {
            if ( null == context )
            {
                final String message = "context is null";
                throw new IllegalArgumentException( message );
            }
            ( (Contextualizable) object ).contextualize( context );
        }
    }
}
