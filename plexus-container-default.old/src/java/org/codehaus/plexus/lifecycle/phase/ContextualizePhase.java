package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class ContextualizePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        Context context = (Context) handler.getEntities().get( "context" );

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
