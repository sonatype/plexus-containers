package org.codehaus.plexus.test.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.test.lifecycle.phase.Miny;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class MinyPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Miny )
        {
            ( (Miny) object ).miny();
        }
    }
}
