package org.codehaus.plexus.component.repository;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Component instantiation description.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentDescriptor
{
    private String alias = null;

    private String role = null;

    private String roleHint = null;

    private String implementation = null;

    private String version = null;

    private String componentType = null;

    private PlexusConfiguration configuration = null;

    private String instantiationStrategy = null;

    private String lifecycleHandler = null;

    private String componentProfile = null;

    private List requirements;

    private String componentFactory;

    private String componentComposer;
    
    private String componentConfigurator;

    private String description;

    private String realmId;

    // ----------------------------------------------------------------------
    // These two fields allow for the specification of an isolated class realm
    // and dependencies that might be specified in a component configuration
    // setup by a user i.e. this is here to allow isolation for components
    // that are not picked up by the discovery mechanism.
    // ----------------------------------------------------------------------

    private boolean isolatedRealm;

    private List dependencies;

    // ----------------------------------------------------------------------

    private ComponentSetDescriptor componentSetDescriptor;

    // ----------------------------------------------------------------------
    //  Instance methods
    // ----------------------------------------------------------------------

    public String getComponentKey()
    {
        if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }

    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append("role: '" + role + "'" );

        key.append( ", implementation: '" + implementation  + "'" );

        if ( roleHint != null )
        {
            key.append( ", role hint: '" + roleHint  + "'" );
        }

        if ( alias != null )
        {
            key.append( ", alias: '" + alias  + "'" );
        }

        return key.toString();
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias( String alias )
    {
        this.alias = alias;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType( String componentType )
    {
        this.componentType = componentType;
    }

    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public boolean hasConfiguration()
    {
        return configuration != null;
    }

    public String getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public void setLifecycleHandler( String lifecycleHandler )
    {
        this.lifecycleHandler = lifecycleHandler;
    }

    public String getComponentProfile()
    {
        return componentProfile;
    }

    public void setComponentProfile( String componentProfile )
    {
        this.componentProfile = componentProfile;
    }

    public void addRequirement( final ComponentRequirement requirement )
    {
        getRequirements().add( requirement );
    }

    public void addRequirements( List requirements )
    {
        getRequirements().addAll( requirements );
    }

    public List getRequirements()
    {
        if ( requirements == null )
        {
            requirements = new ArrayList();
        }
        return requirements;
    }

    public String getComponentFactory()
    {
        return componentFactory;
    }

    public void setComponentFactory( String componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    public String getComponentComposer()
    {
        return componentComposer;
    }

    public void setComponentComposer( String componentComposer )
    {
        this.componentComposer = componentComposer;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    public void setComponentSetDescriptor( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    public ComponentSetDescriptor getComponentSetDescriptor()
    {
        return componentSetDescriptor;
    }

    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    public List getDependencies()
    {
        return dependencies;
    }

    public String getComponentConfigurator()
    {
        return componentConfigurator;
    }

    public void setComponentConfigurator( String componentConfigurator )
    {
        this.componentConfigurator = componentConfigurator;
    }

    public String getRealmId()
    {
        return realmId;
    }

    public void setRealmId( String realmId )
    {
        this.realmId = realmId;
    }

    // Component identity established here!
    public boolean equals(Object other)
    {
        if(!(other instanceof ComponentDescriptor))
        {
            return false;
        }
        else
        {
            ComponentDescriptor otherDescriptor = (ComponentDescriptor) other;
            
            boolean isEqual = true;
            
            String role = getRole();
            String otherRole = otherDescriptor.getRole();
            
            isEqual = isEqual && ( role == otherRole || role.equals( otherRole ) );
            
            String roleHint = getRoleHint();
            String otherRoleHint = otherDescriptor.getRoleHint();
            
            isEqual = isEqual && ( roleHint == otherRoleHint || roleHint.equals( otherRoleHint ) );
            
            return isEqual;
        }
    }
    
    public String toString()
    {
        return this.getClass().getName() + " [role: \'" + getRole() + "\', hint: \'" + getRoleHint() + "\']";
    }
    
    public int hashCode()
    {
        int result = getRole().hashCode() + 1;
        
        String hint = getRoleHint();
        
        if( hint != null )
        {
            result += hint.hashCode();
        }
        
        return result;
    }    
}
