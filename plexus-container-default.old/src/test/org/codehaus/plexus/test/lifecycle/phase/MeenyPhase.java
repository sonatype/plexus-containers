package org.codehaus.plexus.test.lifecycle.phase;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.test.lifecycle.phase.Meeny;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;

public class MeenyPhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Meeny )
        {
            ( (Meeny) object ).meeny();
        }
    }
}
