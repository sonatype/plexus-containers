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
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentImplementationNotFoundException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ArrayListMultimap;

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

    private final Map<String, Multimap<String, ComponentDescriptor<?>>> roleIndex = new TreeMap<String, Multimap<String, ComponentDescriptor<?>>>();

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

    private Multimap<String, ComponentDescriptor<?>> getComponentDescriptors( String role )
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // Get all valid component descriptors
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = roleIndex.get( role );
        if ( roleHintIndex == null )
        {
            roleHintIndex = Multimaps.newHashMultimap();
        }
        return Multimaps.unmodifiableMultimap( roleHintIndex );
    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String role, String roleHint )
    {
        for ( ComponentDescriptor<?> descriptor : getComponentDescriptors( role ).get( roleHint ) )
        {
            if ( isAssignableFrom( type, descriptor.getImplementationClass() ) )
            {
                return (ComponentDescriptor<T>) descriptor;
            }
        }
                
        return null;
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type, String role )
    {
        Map<String, ComponentDescriptor<T>> descriptors = new TreeMap<String, ComponentDescriptor<T>>();
        for ( ComponentDescriptor<?> descriptor : getComponentDescriptors( role ).values() )
        {
            if ( !descriptors.containsKey( descriptor.getRoleHint() ) )
            {
                if ( isAssignableFrom( type, descriptor.getImplementationClass() ) )
                {
                    descriptors.put( descriptor.getRoleHint(), (ComponentDescriptor<T>) descriptor );
                }
            }
        }
        return descriptors;
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type, String role )
    {
        List<ComponentDescriptor<T>> descriptors = new ArrayList<ComponentDescriptor<T>>();
        for ( ComponentDescriptor<?> descriptor : getComponentDescriptors( role ).values() )
        {
            if ( isAssignableFrom( type, descriptor.getImplementationClass() ) )
            {
                descriptors.add( (ComponentDescriptor<T>) descriptor );
            }
        }
        return descriptors;
    }

    @Deprecated
    public ComponentDescriptor<?> getComponentDescriptor( String role, String roleHint, ClassRealm realm )
    {
        // find all realms from our realm to the root realm
        Set<ClassRealm> realms = new HashSet<ClassRealm>();
        for ( ClassRealm r = realm; r != null; r = r.getParentRealm() )
        {
            realms.add( r );
        }

        // get the component descriptors by roleHint
        for ( ComponentDescriptor<?> componentDescriptor : getComponentDescriptors( role ).get( roleHint ) )
        {
            // return the first descriptor from our target realms
            if ( realms.contains( componentDescriptor.getRealm() ) )
            {
                return componentDescriptor;
            }
        }

        return null;
    }

    public void setClassRealm( ClassRealm classRealm )
    {
        this.classRealm = classRealm;
    }

    public void removeComponentRealm( ClassRealm classRealm )
    {
        for ( Multimap<String, ComponentDescriptor<?>> roleHintIndex : roleIndex.values() )
        {
            for ( Iterator<ComponentDescriptor<?>> iterator = roleHintIndex.values().iterator(); iterator.hasNext(); )
            {
                ComponentDescriptor<?> componentDescriptor = iterator.next();
                if ( classRealm.equals( componentDescriptor.getRealm() ) )
                {
                    iterator.remove();
                }

            }
        }
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
        ComponentDescriptor<?> componentDescriptor;
        try
        {
            componentDescriptor = PlexusTools.buildComponentDescriptor( configuration );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentRepositoryException( "Cannot unmarshall component descriptor:", e );
        }

        componentDescriptor.setRealm( classRealm );

        addComponentDescriptor( componentDescriptor );
    }

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor )
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
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = roleIndex.get( role );
        if ( roleHintIndex == null )
        {
            roleHintIndex = new ArrayListMultimap<String, ComponentDescriptor<?>>();
            roleIndex.put( role, roleHintIndex );
        }
        roleHintIndex.put( componentDescriptor.getRoleHint(), componentDescriptor );

        try
        {
            compositionResolver.addComponentDescriptor( componentDescriptor );
        }
        catch ( CompositionException e )
        {
            throw new ComponentRepositoryException( e.getMessage(), e );
        }
    }

    public void validateComponentDescriptor( ComponentDescriptor<?> componentDescriptor )
        throws ComponentImplementationNotFoundException
    {
        if ( componentDescriptor.getRealm() == null )
        {
            // component descriptors are required to have a realmId set.
            componentDescriptor.setRealm( classRealm );
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

}
