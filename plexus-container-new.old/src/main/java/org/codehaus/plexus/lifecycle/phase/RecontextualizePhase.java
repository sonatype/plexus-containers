package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Recontextualizable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class RecontextualizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Context context = (Context) manager.getLifecycleHandler().getEntities().get( "context" );

        if ( object instanceof Recontextualizable )
        {
            if ( null == context )
            {
                final String message = "context is null";
                throw new IllegalArgumentException( message );
            }
            ( (Recontextualizable) object ).recontextualize( context );
        }
    }
}
