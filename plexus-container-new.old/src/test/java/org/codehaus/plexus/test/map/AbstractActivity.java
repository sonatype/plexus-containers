package org.codehaus.plexus.test.map;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractActivity
    implements Activity
{
    private boolean state;

    public boolean getState()
    {
        return state;
    }

    public void execute()
    {
        state = true;
    }
}
