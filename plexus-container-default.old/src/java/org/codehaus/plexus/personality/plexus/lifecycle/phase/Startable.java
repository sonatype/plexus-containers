package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public interface Startable
{
    void start()
        throws Exception;

    void stop()
        throws Exception;
}
