package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.service.repository.ComponentHousing;

public class ComposePhase
    extends AbstractPhase
{
    public void execute( ComponentHousing housing, LifecycleHandler handler )
        throws Exception
    {
        Object object = housing.getComponent();
        ComponentManager componentManager = (ComponentManager) handler.getEntities().get( "comonent.manager" );

        if ( object instanceof Composable )
        {
            if ( null == componentManager )
            {
                final String message = "componentManager is null";
                throw new IllegalArgumentException( message );
            }

            ( (Composable) object ).compose( componentManager );
        }
    }
}
