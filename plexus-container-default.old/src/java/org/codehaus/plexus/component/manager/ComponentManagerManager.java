package org.codehaus.plexus.component.manager;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

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

    void initializeLifecycleHandlerManager( PlexusConfiguration c )
        throws Exception;

    // ----------------------------------------------------------------------
    // Component manager handling
    // ----------------------------------------------------------------------

    ComponentManager findComponentManagerByComponentKey( String componentKey );

    ComponentManager findComponentManagerByComponentInstance( Object component );

    ComponentManager createComponentManager( ComponentDescriptor descriptor, PlexusContainer container )
        throws Exception;

    Map getComponentManagers();

    void associateComponentWithComponentManager( Object component, ComponentManager componentManager );
}
