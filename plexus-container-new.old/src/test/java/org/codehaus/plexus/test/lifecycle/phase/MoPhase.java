package org.codehaus.plexus.test.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.test.lifecycle.phase.Mo;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class MoPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Mo )
        {
            ( (Mo) object ).mo();
        }
    }
}
