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

    /** How many clients are connected to this component. */
    //private int connections;

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

   /* public int getConnections()
    {
        return connections;
    }

    public void setConnections( int connections )
    {
        this.connections = connections;
    }*/

   public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public void setComponentManager( ComponentManager componentManager )
    {
        this.componentManager = componentManager;
    }
}
