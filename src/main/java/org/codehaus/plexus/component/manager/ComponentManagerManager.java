package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.lifecycle.LifecycleHandlerManager;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

import java.util.Map;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentManagerManager
{
    String ROLE = ComponentManagerManager.class.getName();

    void setLifecycleHandlerManager( LifecycleHandlerManager lifecycleHandlerManager );

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    ComponentManager findComponentManagerByComponentKey( String componentKey );

    ComponentManager findComponentManagerByComponentInstance( Object component );

    ComponentManager createComponentManager( ComponentDescriptor descriptor, MutablePlexusContainer container )
        throws UndefinedComponentManagerException, UndefinedLifecycleHandlerException;

    Map getComponentManagers();

    void associateComponentWithComponentManager( Object component, ComponentManager componentManager );

    void unassociateComponentWithComponentManager( Object component );
}
