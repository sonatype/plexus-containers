package org.codehaus.plexus.lifecycle.avalon.phase;

import org.apache.avalon.framework.activity.Initializable;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class InitializePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Initializable )
        {
            ( (Initializable) object ).initialize();
        }
    }
}
