package org.codehaus.plexus.service.repository.instance;

import org.codehaus.plexus.service.repository.ComponentHousing;

/**
 * This ensures a component is only used as a singleton, and is only shutdown when
 * the container shuts down.
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class KeepAliveSingletonInstanceManager
    extends AbstractInstanceManager
{
    private ComponentHousing singleton;

    /** Number of clients using this component */
    private int connections = 0;

    /**
     *
     */
    public KeepAliveSingletonInstanceManager()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#release(java.lang.Object)
     */
    public void release( Object component )
    {
        //Only accept it if it is the same instance.
        if ( singleton.getComponent() == component )
        {
            connections--;
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
        connections++;
        return singleton.getComponent();
    }

    /**
     * @see org.codehaus.plexus.service.repository.instance.InstanceManager#getConnections()
     */
    public int getConnections()
    {
        return connections;
    }

}