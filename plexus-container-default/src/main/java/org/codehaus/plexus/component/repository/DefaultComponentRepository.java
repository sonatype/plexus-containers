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

/**
 * @author Jason van Zyl
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private static String COMPONENTS = "components";

    private static String COMPONENT = "component";

    private PlexusConfiguration configuration;

    private class ComponentRealmDescriptorMaps
    {
        /**
         * Map&lt;RealmId, RealmMaps>.
         */
        private Map realmMaps = new HashMap();

        public boolean containsKey( String role, String roleHint, ClassRealm realm )
        {
            if ( realm == null )
            {
                return false;
            }

            Map maps = (Map) realmMaps.get( realm.getId() );

            if ( maps != null &&  maps.containsKey( role ) )
            {
                Map hints = (Map) maps.get( role );

                return hints.containsKey( roleHint );
            }

            return containsKey( role, roleHint, realm.getParentRealm() );
        }

        public Map getComponentDescriptorMap( String role, ClassRealm realm )
        {
            if ( realm == null )
            {
                return null;
            }

            Map maps = (Map) realmMaps.get( realm.getId() );

            if ( maps != null && maps.containsKey( role ) )
            {
                return (Map) maps.get( role );
            }

            return getComponentDescriptorMap( role, realm.getParentRealm() );
        }

        public ComponentDescriptor getComponentDescriptor( String role, String roleHint, ClassRealm realm )
        {
            if ( realm == null )
            {
                return null;
            }

            if ( roleHint == null )
            {
                roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
            }

            // System.out.println("GetComponentDescriptor( " + role + ", " + roleHint + ", " + realm.getId() + ")" );

            Map roleMap = getComponentDescriptorMap( role, realm );

            if ( roleMap != null )
            {
                ComponentDescriptor desc = (ComponentDescriptor) roleMap.get( roleHint );
                if ( desc != null )
                {
                    // System.out.println("  Found" );
                    return desc;
                }
            }
            // System.out.println("  Not found" +(realm.getParentRealm()==null?"":", trying parent " + realm.getParentRealm().getId()));

            return getComponentDescriptor( role, roleHint, realm.getParentRealm() );
        }

        public Map getRealmMap( String realmId )
        {
            Map rm = (Map) realmMaps.get( realmId );

            if ( rm == null )
            {
                realmMaps.put( realmId, rm = new HashMap() );
            }

            return rm;
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

    public Map getComponentDescriptorMap( String role, ClassRealm realm )
    {
        return componentRealmDescriptorMaps.getComponentDescriptorMap( role, realm );
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

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            addComponentDescriptor( componentConfigurations[i] );
        }
    }

    // ----------------------------------------------------------------------
    // Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( PlexusConfiguration configuration )
        throws ComponentRepositoryException
    {
        ComponentDescriptor componentDescriptor = null;

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

        Map maps = componentRealmDescriptorMaps.getRealmMap( componentDescriptor.getRealmId() );

        String role = componentDescriptor.getRole();

        String roleHint = componentDescriptor.getRoleHint();

        Map map = (Map) maps.get( role );

        if ( map == null )
        {
            map = new HashMap();

            maps.put( role, map );
        }

        map.put( roleHint, componentDescriptor );

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
