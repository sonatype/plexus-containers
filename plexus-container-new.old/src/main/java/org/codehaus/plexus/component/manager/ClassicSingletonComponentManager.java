package org.codehaus.plexus.component.manager;

/**
 * This ensures only a single manager of a a component exists. Once no
 * more connections for this component exists it is disposed.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 *
 * @version $Id$
 */
public class ClassicSingletonComponentManager
    extends AbstractComponentManager
{
    private Object singleton;

    public ClassicSingletonComponentManager()
    {
    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#release(java.lang.Object)
     */
    public void release( Object component )
    {
        if ( singleton == component )
        {
            decrementConnectionCount();

            if ( ! connected() )
            {
                dispose();
            }
        }
        else
        {
            getLogger().warn( "Component returned which is not the same manager. Ignored. component=" + component );
        }
    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#dispose()
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
     * @see org.codehaus.plexus.component.manager.ComponentManager#getComponent()
     */
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