package org.codehaus.plexus.lifecycle.phase;

import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.Reparameterizable;
import org.codehaus.plexus.component.manager.ComponentManager;

public class ReparameterizePhase
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
            ( (Reparameterizable) object ).parameterize( parameters );
        }
    }
}
