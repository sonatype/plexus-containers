/*
 * $Id$
 */

package org.codehaus.plexus.component.manager;

import java.util.Map;

/**
 * An instance manager that does not track the objects that have been created.
 * Instead it ensures that it is always used with a single type of 
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class NonTrackingInstanceManager
    implements InstanceManager
{
    private ComponentManager myComponentManager;

    private String myComponentManagerId;

    public void register( Object component, ComponentManager componentManager )
    {
        if ( myComponentManager == null )
        {
            myComponentManager = componentManager;
            
            myComponentManagerId = componentManager.getId();
        }
        else if ( !myComponentManagerId.equals( componentManager.getId() ) )
        {
            throw new IllegalStateException( "Component implementation "
                    + componentManager.getComponentDescriptor().getImplementation()
                    + " used with differing instantiation strategies" );
        }
    }

    public ComponentManager findComponentManager( Object component )
    {
        return myComponentManager;
    }
}
