package org.codehaus.plexus.service.repository.instance;

import org.codehaus.plexus.service.repository.ComponentHousing;

/**
 * This ensures only a single instance of a a component exists. Once no
 * more connections for this component exists it is disposed.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class ClassicSingletonInstanceManager
    extends AbstractInstanceManager
{
    private ComponentHousing singleton;

    /**
     *
     */
    public ClassicSingletonInstanceManager()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#release(java.lang.Object)
     */
    public void release( Object component )
    {
        if ( singleton.getComponent() == component )
        {
            decrementConnectionCount();

            if ( ! connected() )
            {
                dispose();
            }
        }
        else
        {
            getLogger().warn( "Component returned which is not the same instance. Ignored. component=" + component );
        }
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#dispose()
     */
    public void dispose()
    {
        //wait for all the clients to return all the components
        //Do we do this in a seperate thread? or block the current thread??
        //TODO
        if ( singleton != null )
        {
            endComponentLifecycle( singleton );
            singleton = null;
        }
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getComponent()
     */
    public Object getComponent() throws Exception
    {
        if ( singleton == null )
        {
            singleton = newHousingInstance();
        }

        incrementConnectionCount();

        return singleton.getComponent();
    }
}