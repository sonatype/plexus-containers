package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public interface Startable
{
    void start()
        throws StartingException;

    void stop()
        throws StoppingException;
}
