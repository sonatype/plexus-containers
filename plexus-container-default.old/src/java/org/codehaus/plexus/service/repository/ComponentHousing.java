package org.codehaus.plexus.service.repository;

/**
 * Holds the actual instantiated component
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentHousing
{
    /** Component Manager that oversees this instance. */
    private ComponentManager componentManager;

    /** The component being housed. */
    private Object component;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Object getComponent()
    {
        return component;
    }

    public void setComponent( Object component )
    {
        this.component = component;
    }

    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public void setComponentManager( ComponentManager componentManager )
    {
        this.componentManager = componentManager;
    }
}
