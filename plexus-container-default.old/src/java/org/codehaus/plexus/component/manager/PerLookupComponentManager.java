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
    public PerLookupComponentManager()
    {
        super();
    }

    public void dispose()
    {
        //nothing todo as component has lifecycle
        //ended on release
    }

    public Object getComponent()
        throws Exception
    {
        Object component = createComponentInstance();

        return component;
    }

    public void release( Object component )
    {
        endComponentLifecycle( component );
    }
}
