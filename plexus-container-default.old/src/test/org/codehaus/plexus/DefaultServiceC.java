package org.codehaus.plexus;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

public class DefaultServiceC
    implements ServiceC, Startable
{
    boolean started = false;
    boolean stopped = false;

    public void start()
    {
        started = true;
    }

    public void stop()
    {
        stopped = true;
    }
}
