package org.codehaus.plexus.personality.plexus.lifecycle.phase;

public interface Suspendable
{
    void suspend();

    void resume();
}
