package org.codehaus.plexus.component.manager;

/**
 * Creates a new component manager for every lookup
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PerLookupComponentManager
    extends AbstractComponentManager
{
    /**
     *
     */
    public PerLookupComponentManager()
    {
        super();
    }

    public void dispose()
    {
        //nothing todo as component has lifecycle
        //ended on release
    }

    /* (non-Javadoc)
     * @see org.codehaus.plexus.component.repository.manager.InstanceManager#getComponent()
     */
    public Object getComponent()
        throws Exception
    {
        Object component = createComponentInstance();

        return component;
    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#release(java.lang.Object)
     */
    public void release( Object component )
    {
        endComponentLifecycle( component );
    }
}
