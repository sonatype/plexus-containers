/*
 * $Id$
 */

package org.codehaus.plexus.component.manager;

/**
 * Instance managers keep track of component instances created by a
 * component manager.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public interface InstanceManager
{
    void register( Object component, ComponentManager componentManager );

    ComponentManager findComponentManager( Object component );
}
