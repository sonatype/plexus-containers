package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * A slow starting component that checks that sleeps during its Start phase.
 *
 * Configuration: 
 *   delay - number of milliseconds to sleep during start() 
 * @author Ben Walding
 */
public class SlowComponent implements Startable
{
    public static final String ROLE = SlowComponent.class.getName();
    
    /* Number of ms to sleep during start() */
    private long delay;
    
    public void start() throws Exception
    {
        Thread.sleep( delay );
    }

    public void stop() throws Exception
    {
        //Nothing to do.
    }

}