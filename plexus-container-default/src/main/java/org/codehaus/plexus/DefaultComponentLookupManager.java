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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.manager.UndefinedComponentManagerException;
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

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return lookup( role, null, null );
    }

    public Object lookup( String role, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( role, null, realm );
    }

    public Object lookup( Class componentClass )
        throws ComponentLookupException
    {
        return lookup( componentClass.getName(), null, null );
    }

    public Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( componentClass.getName(), null, realm );
    }

    // ----------------------------------------------------------------------------
    // Role + Hint
    // ----------------------------------------------------------------------------

    public Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        return lookup( role.getName(), roleHint, realm );
    }

    public Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role, roleHint, null );
    }

    public Object lookup( Class role, String roleHint )
        throws ComponentLookupException
    {
        return lookup( role.getName(), roleHint, null );
    }

    public Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        if ( realm == null )
        {
            realm = container.getLookupRealm();
        }

        Object component = lookupInternal( role, roleHint, realm );
        return component;
    }

    private Object lookupInternal( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }
        if ( roleHint == null )
        {
            roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }

        // lookup an existing component
        ComponentManager componentManager = container.getComponentManagerManager().findComponentManagerByComponentKey( role, roleHint, realm );

        // The first time we lookup a component a component manager will not exist so we ask the
        // component manager manager to create a component manager for us. Also if we are reloading
        // components then we'll also get a new component manager.
        if ( componentManager == null )
        {
            ComponentDescriptor descriptor = container.getComponentRepository().getComponentDescriptor( role, roleHint,
                realm );

            if ( descriptor == null )
            {
                String message = "Component descriptor cannot be found in the component repository: " + role + " [" + roleHint + "]" + " (lookup realm: " + realm + ").";

                throw new ComponentLookupException( message, role, roleHint, realm );
            }

            componentManager = createComponentManager( descriptor, role, roleHint );
        }

        Object component = getComponent( componentManager );

        return component;
    }

    private Object getComponent( ComponentManager componentManager )
        throws ComponentLookupException
    {
        Object component;
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

    public Map<String, Object> lookupMap( String role )
        throws ComponentLookupException
    {
        return lookupMapInternal( role, null );
    }

    public Map<String, Object> lookupMap( String role, List<String> hints )
        throws ComponentLookupException
    {
        return lookupMapInternal( role, hints );
    }

    public Map<String, Object> lookupMap( Class role )
        throws ComponentLookupException
    {
        return lookupMapInternal( role.getName(), null );
    }

    public Map<String, Object> lookupMap( Class role, List<String> hints )
        throws ComponentLookupException
    {
        return lookupMapInternal( role.getName(), hints );
    }

    /**
     * Return a Map of components for a given role keyed by the component role hint.
     */
    private Map<String, Object> lookupMapInternal( String role, List<String> hints )
        throws ComponentLookupException
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // if no hints provided, get all valid hints for this role
        if ( hints == null )
        {
            ComponentRepository repository = container.getComponentRepository();
            Map<String, ComponentDescriptor> componentDescriptors = repository.getComponentDescriptorMap( role, null );

            hints = new ArrayList<String>();
            if ( componentDescriptors != null )
            {
                hints.addAll( componentDescriptors.keySet() );
            }
        }

        // lookup each component using role + hint
        Map<String, Object> components = new LinkedHashMap<String, Object>();
        for ( String hint : hints )
        {
            // todo dain catch the exception... it isn't the callers problem when one component in a collection fails
            Object component = lookupInternal( role, hint, null );
            components.put( hint, component );
        }

        return components;
    }

    // ----------------------------------------------------------------------------
    // Lists
    // ----------------------------------------------------------------------------

    public List<Object> lookupList( String role )
        throws ComponentLookupException
    {
        return lookupListInternal( role, null );
    }

    public List<Object> lookupList( Class role )
        throws ComponentLookupException
    {
        return lookupListInternal( role.getName(), null );
    }

    public List<Object> lookupList( String role, List<String> hints )
        throws ComponentLookupException
    {
        return lookupListInternal( role, hints );
    }

    public List<Object> lookupList( Class role, List<String> hints )
        throws ComponentLookupException
    {
        return lookupListInternal( role.getName(), hints );
    }

    /**
     * Return a List of components for a given role and list of hints.
     */
    public List<Object> lookupListInternal( String role, List<String> hints )
        throws ComponentLookupException
    {
        Map<String, Object> componentIndex = lookupMapInternal( role, hints );
        return new ArrayList<Object>( componentIndex.values() );
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

    public ComponentManager createComponentManager( ComponentDescriptor descriptor, String role, String roleHint )
        throws ComponentLookupException
    {
        ComponentManager componentManager;

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
