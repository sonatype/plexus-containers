package org.codehaus.plexus.component.manager;



/**
 * This ensures a component is only used as a singleton, and is only shutdown when
 * the container shuts down.
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class KeepAliveSingletonComponentManager
    extends AbstractComponentManager
{
    private Object singleton;

    public void release( Object component )
        throws Exception
    {
        if ( singleton == component )
        {
            decrementConnectionCount();
        }
        else
        {
            getLogger().warn( "Component returned which is not the same manager. Ignored. component=" + component );
        }
    }

    public void dispose()
        throws Exception
    {
        //wait for all the clients to return all the components
        //Do we do this in a seperate thread? or block the current thread??
        //TODO
        if ( singleton != null )
        {
            endComponentLifecycle( singleton );
        }
    }

    public Object getComponent() throws Exception
    {
        if ( singleton == null )
        {
            singleton = createComponentInstance();
        }

        incrementConnectionCount();

        return singleton;
    }
}
