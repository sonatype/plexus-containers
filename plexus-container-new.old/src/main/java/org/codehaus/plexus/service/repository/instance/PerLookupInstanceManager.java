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

    /* (non-Javadoc)
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getComponent()
     */
    public Object getComponent() throws Exception
    {
        ComponentHousing h = newHousingInstance();

        putHousing( h.getComponent(), h );

        incrementConnectionCount();

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
            decrementConnectionCount();

            endComponentLifecycle( h );
        }
    }
}
