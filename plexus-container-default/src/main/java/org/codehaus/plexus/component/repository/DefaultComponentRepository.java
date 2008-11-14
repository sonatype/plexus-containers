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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * @author Jason van Zyl
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private static final String COMPONENTS = "components";

    private static final String COMPONENT = "component";

    private PlexusConfiguration configuration;

    private class ComponentRealmDescriptorMaps
    {
        /** Map<realmId, Map<role, Map<roleHint, ComponentDescriptor>>> */
        private final Map<String, Map<String, Map<String, ComponentDescriptor>>> realmIndex = new LinkedHashMap<String, Map<String, Map<String, ComponentDescriptor>>>();

        public boolean containsKey( String role, String roleHint, ClassRealm realm )
        {
            return getComponentDescriptor( role, roleHint, realm ) != null;
        }

        public Map<String, ComponentDescriptor> getComponentDescriptorMap( String role, ClassRealm realm )
        {
            // verify arguments
            if ( role == null )
            {
                throw new NullPointerException( "role is null" );
            }

            // determine the roleIndexes to search
            Collection<Map<String, Map<String, ComponentDescriptor>>> roleIndexes = new ArrayList<Map<String, Map<String, ComponentDescriptor>>>();
            if ( realm == null )
            {
                // search all realms
                roleIndexes.addAll( realmIndex.values() );
            }
            else
            {
                // serach from specified realm up throught the parent chain
                for ( ; realm != null; realm = realm.getParentRealm() )
                {
                    Map<String, Map<String, ComponentDescriptor>> roleIndex = realmIndex.get( realm.getId() );
                    if ( roleIndex != null )
                    {
                        roleIndexes.add( roleIndex );
                    }
                }
            }

            // Get all valid component descriptors
            Map<String, ComponentDescriptor> componentDescriptors = new LinkedHashMap<String, ComponentDescriptor>();
            for ( Map<String, Map<String, ComponentDescriptor>> roleIndex : roleIndexes )
            {
                Map<String, ComponentDescriptor> roleHintIndex = roleIndex.get( role );
                if ( roleHintIndex != null )
                {
                    for ( Entry<String, ComponentDescriptor> entry : roleHintIndex.entrySet() )
                    {
                        // when there are multiple component descriptors with the same hint favor the one in the realm
                        // closest to the specifed realm or when searching all realms, the realm that was registered first
                        if ( !componentDescriptors.containsKey( entry.getKey() ) )
                        {
                            componentDescriptors.put( entry.getKey(), entry.getValue() );
                        }
                    }
                }
            }

            return componentDescriptors;
        }

        public ComponentDescriptor getComponentDescriptor( String role, String roleHint, ClassRealm realm )
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

            // get the component descriptors by roleHint
            Map<String, ComponentDescriptor> roleHintIndex = getComponentDescriptorMap( role, realm );

            // get the component descriptor if one exists
            ComponentDescriptor componentDescriptor = roleHintIndex.get( roleHint );
            return componentDescriptor;
        }


        public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        {
            // Get the index of roles
            Map<String, Map<String, ComponentDescriptor>> roleIndex = realmIndex.get( componentDescriptor.getRealmId() );
            if ( roleIndex == null )
            {
                roleIndex = new HashMap<String, Map<String, ComponentDescriptor>>();
                realmIndex.put( componentDescriptor.getRealmId(), roleIndex );
            }

            // get the index of role hints
            Map<String, ComponentDescriptor> roleHintIndex = roleIndex.get( componentDescriptor.getRole() );
            if ( roleHintIndex == null )
            {
                roleHintIndex = new HashMap<String, ComponentDescriptor>();

                roleIndex.put( componentDescriptor.getRole(), roleHintIndex );
            }

            // add the component descriptor
            roleHintIndex.put( componentDescriptor.getRoleHint(), componentDescriptor );
        }

        public void removeRealmMap( String realmId )
        {
            realmIndex.remove( realmId );
        }
    }

    private ComponentRealmDescriptorMaps componentRealmDescriptorMaps = new ComponentRealmDescriptorMaps();

    private CompositionResolver compositionResolver = new DefaultCompositionResolver();

    private ClassRealm classRealm;

    public DefaultComponentRepository()
    {
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    protected PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean hasComponent( String role, ClassRealm realm )
    {
        return componentRealmDescriptorMaps.containsKey( role, PlexusConstants.PLEXUS_DEFAULT_HINT, realm );
    }

    public boolean hasComponent( String role, String roleHint, ClassRealm realm )
    {
        return componentRealmDescriptorMaps.containsKey( role, roleHint, realm );
    }

    public Map<String, ComponentDescriptor> getComponentDescriptorMap( String role, ClassRealm realm )
    {
        Map<String, ComponentDescriptor> descriptors = componentRealmDescriptorMaps.getComponentDescriptorMap( role, realm );
        return Collections.unmodifiableMap( new LinkedHashMap<String, ComponentDescriptor>(descriptors) );
    }

    public ComponentDescriptor getComponentDescriptor( String role, ClassRealm realm )
    {
        return getComponentDescriptor( role, PlexusConstants.PLEXUS_DEFAULT_HINT, realm );
    }

    public ComponentDescriptor getComponentDescriptor( String role, String roleHint, ClassRealm realm )
    {
        return componentRealmDescriptorMaps.getComponentDescriptor( role, roleHint, realm );
    }

    public void setClassRealm( ClassRealm classRealm )
    {
        this.classRealm = classRealm;
    }

    public void removeComponentRealm( ClassRealm classRealm )
    {
        componentRealmDescriptorMaps.removeRealmMap( classRealm.getId() );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void configure( PlexusConfiguration configuration )
    {
        this.configuration = configuration;
    }

    public void initialize()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptors();
    }

    public void initializeComponentDescriptors()
        throws ComponentRepositoryException
    {
        initializeComponentDescriptorsFromUserConfiguration();
    }

    private void initializeComponentDescriptorsFromUserConfiguration()
        throws ComponentRepositoryException
    {
        PlexusConfiguration[] componentConfigurations = configuration.getChild( COMPONENTS ).getChildren( COMPONENT );

        for ( PlexusConfiguration componentConfiguration : componentConfigurations )
        {
            addComponentDescriptor( componentConfiguration );
        }
    }

    // ----------------------------------------------------------------------
    // Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor;

        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        componentDescriptor.setRealmId( classRealm.getId() );

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException
    {
        try
        {
            validateComponentDescriptor( componentDescriptor );
        }
        catch ( ComponentImplementationNotFoundException e )
        {
            throw new ComponentRepositoryException( "Component descriptor validation failed: ", e );
        }

        componentRealmDescriptorMaps.addComponentDescriptor( componentDescriptor );

        try
        {
            compositionResolver.addComponentDescriptor( componentDescriptor );
        }
        catch ( CompositionException e )
        {
            throw new ComponentRepositoryException( e.getMessage(), e );
        }
    }

    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        if ( componentDescriptor.getRealmId() == null )
        {
            // component descriptors are required to have a realmId set.
            componentDescriptor.setRealmId( classRealm.getId() );
            // log.warn( "Componentdescriptor " + componentDescriptor + " is missing realmId - using " +
            // componentDescriptor.getRealmId() );
            // throw new ComponentImplementationNotFoundException( "ComponentDescriptor is missing realmId" );
        }
        // Make sure the component implementation classes can be found.
        // Make sure ComponentManager implementation can be found.
        // Validate lifecycle.
        // Validate the component configuration.
        // Validate the component profile if one is used.
    }

    public List getComponentDependencies( ComponentDescriptor componentDescriptor )
    {
        return compositionResolver.getRequirements( componentDescriptor.getRole(), componentDescriptor.getRoleHint() );
    }
}
