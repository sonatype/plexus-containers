package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.codehaus.plexus.component.manager.ComponentManager;

public class ParameterizePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Parameters parameters = (Parameters) manager.getLifecycleHandler().getEntities().get( "parameters" );

        if ( object instanceof Parameterizable )
        {
            if ( null == parameters )
            {
                final String message = "parameters is null";
                throw new IllegalArgumentException( message );
            }
            ( (Parameterizable) object ).parameterize( parameters );
        }
    }
}
