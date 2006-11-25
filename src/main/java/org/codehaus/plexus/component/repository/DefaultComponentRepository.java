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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
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

    private Map componentDescriptorMaps;

    private Map componentDescriptors;

    private CompositionResolver compositionResolver;

    private ClassRealm classRealm;

    public DefaultComponentRepository()
    {
        componentDescriptors = new HashMap();

        componentDescriptorMaps = new HashMap();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    protected PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public boolean hasComponent( String role )
    {
        return componentDescriptors.containsKey( role );
    }

    public boolean hasComponent( String role, String roleHint )
    {
        return componentDescriptors.containsKey( role + roleHint );
    }

    public Map getComponentDescriptorMap( String role )
    {
        return (Map) componentDescriptorMaps.get( role );
    }

    public ComponentDescriptor getComponentDescriptor( String key )
    {
        return (ComponentDescriptor) componentDescriptors.get( key );
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
    //  Component Descriptor processing.
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

        String role = componentDescriptor.getRole();

        String roleHint = componentDescriptor.getRoleHint();

        if ( roleHint != null )
        {
            if ( componentDescriptors.containsKey( role ) )
            {
                ComponentDescriptor desc = (ComponentDescriptor) componentDescriptors.get( role );
                if ( desc.getRoleHint() == null )
                {
                    String message = "Component descriptor " + componentDescriptor.getHumanReadableKey()
                        + " has a hint, but implementation " + desc.getImplementation() + " doesn't";
                    throw new ComponentRepositoryException( message );
                }
            }

            Map map = (Map) componentDescriptorMaps.get( role );

            if ( map == null )
            {
                map = new HashMap();

                componentDescriptorMaps.put( role, map );
            }

            map.put( roleHint, componentDescriptor );
        }
        else
        {
            if ( componentDescriptorMaps.containsKey( role ) )
            {
                String message = "Component descriptor " + componentDescriptor.getHumanReadableKey() +
                    " has no hint, but there are other implementations that do";
                throw new ComponentRepositoryException( message );
            }
            else if ( componentDescriptors.containsKey( role ) )
            {
                if ( !componentDescriptors.get( role ).equals( componentDescriptor ) )
                {
                    String message = "Component role " + role +
                        " is already in the repository and different to attempted addition of " +
                        componentDescriptor.getHumanReadableKey();
                    throw new ComponentRepositoryException( message );
                }
            }
        }

        try
        {
            compositionResolver.addComponentDescriptor( componentDescriptor );
        }
        catch ( CompositionException e )
        {
            throw new ComponentRepositoryException( e.getMessage(), e );
        }

        componentDescriptors.put( componentDescriptor.getComponentKey(), componentDescriptor );
        
        // We need to be able to lookup by role only (in non-collection situations), even when the 
        // component has a roleHint.
        if ( !componentDescriptors.containsKey( role ) )
        {
            componentDescriptors.put( role, componentDescriptor );
        }
    }

    public void validateComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        // Make sure the component implementation classes can be found.
        // Make sure ComponentManager implementation can be found.
        // Validate lifecycle.
        // Validate the component configuration.
        // Validate the component profile if one is used.
    }

    public List getComponentDependencies( ComponentDescriptor componentDescriptor )
    {
        return compositionResolver.getRequirements( componentDescriptor.getComponentKey() );
    }
}
