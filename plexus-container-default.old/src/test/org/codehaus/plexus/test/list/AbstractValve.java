package org.codehaus.plexus.test.list;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractValve
    implements Valve
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
