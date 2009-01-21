package org.codehaus.plexus.test;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

public class DefaultCircularComponent
    implements CircularComponent, Startable
{
    private PlexusContainer container;
    private String lookup;
    private CircularComponent requirement;

    public void start()
        throws StartingException
    {
        try
        {
            if (lookup != null) {
                container.lookup( CircularComponent.class, lookup );
            }
        }
        catch ( Exception e )
        {
            throw new StartingException("failed", e);
        }
    }

    public void stop()
        throws StoppingException
    {
    }
}