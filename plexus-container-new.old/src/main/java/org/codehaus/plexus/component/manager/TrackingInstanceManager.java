/*
 * $Id$
 */

package org.codehaus.plexus.component.manager;

import java.util.Map;

/**
 * An instance manager that tracks the objects that have been created.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class TrackingInstanceManager
    implements InstanceManager
{
    private Map instances = new java.util.HashMap();

    public void register( Object component, ComponentManager componentManager )
    {
        instances.put( component, componentManager );
    }

    public ComponentManager findComponentManager( Object component )
    {
        return (ComponentManager) instances.get( component );
    }
}
