package org.codehaus.plexus;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.UndefinedComponentManagerException;
import org.codehaus.plexus.component.manager.ComponentManagerManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public class DefaultComponentLookupManager
    implements MutableComponentLookupManager
{
    private MutablePlexusContainer container;

    // ----------------------------------------------------------------------
    // Component Lookup
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Try to lookup the component manager for the requested component.
    //
    // component manager exists:
    // -> return a component from the component manager.
    //
    // component manager doesn't exist;
    // -> lookup component descriptor for the requested component.
    // -> instantiate component manager for this component.
    // -> track the component manager for this component by the component class name.
    // -> return a component from the component manager.
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------------
    // Role + Hint
    // ----------------------------------------------------------------------------

    public <T> T lookup( Class<T> type, String role, String roleHint ) throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }

        // lookup an existing component
        ComponentManagerManager componentManagerManager = container.getComponentManagerManager();
        ComponentManager<T> componentManager = componentManagerManager.findComponentManager( type, role, roleHint );

        // The first time we lookup a component a component manager will not exist so we ask the
        // component manager manager to create a component manager for us. Also if we are reloading
        // components then we'll also get a new component manager.
        if ( componentManager == null )
        {
            ComponentRepository componentRepository = container.getComponentRepository();
            ComponentDescriptor<T> descriptor = componentRepository.getComponentDescriptor( type, role, roleHint );
                        
            if ( descriptor == null )
            {
                String message = "Component descriptor cannot be found in the component repository: " + role + " [" + roleHint + "].";

                throw new ComponentLookupException( message, role, roleHint );
            }

            componentManager = createComponentManager( descriptor, role, roleHint );
        }

        T component = getComponent( componentManager );

        return component;
    }

    private <T> T getComponent( ComponentManager<T> componentManager )
        throws ComponentLookupException
    {
        T component;
        try
        {
            component = componentManager.getComponent( );
        }
        catch ( ComponentInstantiationException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be created.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
        }
        catch ( ComponentLifecycleException e )
        {
            throw new ComponentLookupException(
                "Unable to lookup component '" + componentManager.getRole() + "', it could not be started.",
                componentManager.getRole(), componentManager.getRoleHint(), componentManager.getRealm(), e );
        }

        container.getComponentManagerManager().associateComponentWithComponentManager( component, componentManager );

        return component;
    }

    // ----------------------------------------------------------------------------
    // Maps
    // ----------------------------------------------------------------------------

    public <T> Map<String, T> lookupMap( Class<T> type, String role, List<String> hints )
        throws ComponentLookupException
    {
        // verify arguments
        if ( type == null )
        {
            throw new NullPointerException( "type is null" );
        }
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        if ( hints == null )
        {
            ComponentRepository repository = container.getComponentRepository();
            Map<String, ComponentDescriptor<T>> componentDescriptors = repository.getComponentDescriptorMap( type, role );

            hints = new ArrayList<String>();
            if ( componentDescriptors != null )
            {
                hints.addAll( componentDescriptors.keySet() );
            }
        }

        // lookup each component using role + hint
        Map<String, T> components = new LinkedHashMap<String, T>();
        for ( String hint : hints )
        {
            // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
            T component = lookup( type, role, hint );
            components.put( hint, component );
        }

        return components;
    }

    // ----------------------------------------------------------------------------
    // Lists
    // ----------------------------------------------------------------------------

    public <T> List<T> lookupList( Class<T> type, String role, List<String> hints ) throws ComponentLookupException
    {
        Map<String, T> componentIndex = lookupMap( type, role, hints );
        return new ArrayList<T>( componentIndex.values() );
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public void setContainer( MutablePlexusContainer container )
    {
        this.container = container;
    }

    // ----------------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------------

    public <T> ComponentManager<T> createComponentManager( ComponentDescriptor<T> descriptor, String role, String roleHint )
        throws ComponentLookupException
    {
        ComponentManager<T> componentManager;

        try
        {
            componentManager = container.getComponentManagerManager().createComponentManager( descriptor, container, role, roleHint );
        }
        catch ( UndefinedComponentManagerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getRole() + " [" + descriptor.getRoleHint() + "], so we cannot provide a component instance.";

            throw new ComponentLookupException( message, role, roleHint, null, e );
        }
        catch ( UndefinedLifecycleHandlerException e )
        {
            String message = "Cannot create component manager for " + descriptor.getRole() + " [" + descriptor.getRoleHint() + "], so we cannot provide a component instance.";

            throw new ComponentLookupException( message, role, roleHint, null, e );
        }

        return componentManager;
    }
}
