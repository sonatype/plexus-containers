package org.codehaus.plexus.test;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

public class DefaultServiceC
    implements ServiceC, Startable
{
    public boolean started = false;
    public boolean stopped = false;

    public void start()
    {
        started = true;
    }

    public void stop()
    {
        stopped = true;
    }
}
