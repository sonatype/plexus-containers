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
    public void dispose()
    {
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
