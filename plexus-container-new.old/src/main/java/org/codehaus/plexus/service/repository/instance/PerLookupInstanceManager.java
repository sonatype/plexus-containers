package org.codehaus.plexus.service.repository.instance;

import org.codehaus.plexus.service.repository.ComponentHousing;

/**
 * Creates a new component instance for every lookup
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PerLookupInstanceManager
    extends AbstractMultipleInstanceManager
{
    private int connections = 0;

    /**
     *
     */
    public PerLookupInstanceManager()
    {
        super();
    }

    public void dispose()
    {
        //nothing todo as component has lifecycle
        //ended on release
    }

    /**
     * Return the current number of components this manager has given out
     * which have not yet been returned.
     *
     * @return
     */
    public int getConnections()
    {
        return connections;
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getComponent()
     */
    public Object getComponent() throws Exception
    {
        ComponentHousing h = newHousingInstance();
        putHousing( h.getComponent(), h );
        connections++;
        return h.getComponent();
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#release(java.lang.Object)
     */
    public void release( Object component )
    {
        ComponentHousing h = removeHousing( component );
        if ( h != null )
        {
            connections--;
            endComponentLifecycle( h );
        }
    }
}
