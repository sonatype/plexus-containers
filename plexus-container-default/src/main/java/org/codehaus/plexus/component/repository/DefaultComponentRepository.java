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

import static org.codehaus.plexus.component.CastUtils.isAssignableFrom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.codehaus.plexus.ClassRealmUtil;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CycleDetectedInComponentGraphException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Jason van Zyl
 */
public class DefaultComponentRepository
    extends AbstractLogEnabled
    implements ComponentRepository
{
    private final Map<ClassRealm, SortedMap<String, Multimap<String, ComponentDescriptor<?>>>> index = new LinkedHashMap<ClassRealm, SortedMap<String, Multimap<String, ComponentDescriptor<?>>>>();

    private final CompositionResolver compositionResolver = new DefaultCompositionResolver();

    public DefaultComponentRepository()
    {
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    private Multimap<String, ComponentDescriptor<?>> getComponentDescriptors( String role )
    {
        // verify arguments
        if ( role == null )
        {
            throw new NullPointerException( "role is null" );
        }

        // determine realms to search
        Set<ClassRealm> realms = ClassRealmUtil.getContextRealms( null );
        if ( realms.isEmpty() )
        {
            realms.addAll( index.keySet() );
        }

        
        // Get all valid component descriptors
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = LinkedHashMultimap.create();
        for ( ClassRealm realm : realms )
        {
            SortedMap<String, Multimap<String, ComponentDescriptor<?>>> roleIndex = index.get( realm );
            if (roleIndex != null) {
                Multimap<String, ComponentDescriptor<?>> descriptors = roleIndex.get( role );
                if ( descriptors != null )
                {
                    roleHintIndex.putAll( descriptors );
                }
            }
        }
        return Multimaps.unmodifiableMultimap( roleHintIndex );
    }

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String role, String roleHint )
    {
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = getComponentDescriptors( role );

        Collection<ComponentDescriptor<?>> descriptors;

        if ( StringUtils.isNotEmpty( roleHint ) )
        {
            // specific role hint -> get only those
            descriptors = roleHintIndex.get( roleHint );
        }
        else
        {
            // missing role hint -> get all (wildcard)
            Collection<ComponentDescriptor<?>> allDescriptors = new ArrayList<ComponentDescriptor<?>>();

            descriptors = roleHintIndex.get( PlexusConstants.PLEXUS_DEFAULT_HINT );
            if ( descriptors != null )
            {
                allDescriptors.addAll( descriptors );
            }

            for ( String hint : roleHintIndex.keySet() )
            {
                descriptors = roleHintIndex.get( hint );
                if ( descriptors != null )
                {
                    allDescriptors.addAll( descriptors );
                }
            }

            descriptors = allDescriptors;
        }

        for ( ComponentDescriptor<?> descriptor : descriptors )
        {
            Class<?> implClass = descriptor.getImplementationClass();
            if ( isAssignableFrom( type, implClass ) )
            {
                return (ComponentDescriptor<T>) descriptor;
            }
            else if ( Object.class == implClass && role.equals( type.getName() ) )
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

    public void removeComponentRealm( ClassRealm classRealm )
    {
        index.remove( classRealm );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Component Descriptor processing.
    // ----------------------------------------------------------------------

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor ) 
        throws CycleDetectedInComponentGraphException
    {
        ClassRealm classRealm = componentDescriptor.getRealm();
        SortedMap<String, Multimap<String, ComponentDescriptor<?>>> roleIndex = index.get( classRealm );
        if (roleIndex == null) {
            roleIndex = new TreeMap<String, Multimap<String, ComponentDescriptor<?>>>();
            index.put(classRealm,  roleIndex);
        }

        String role = componentDescriptor.getRole();
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = roleIndex.get( role );
        if ( roleHintIndex == null )
        {
            roleHintIndex = LinkedHashMultimap.create();
            roleIndex.put( role, roleHintIndex );
        }
        roleHintIndex.put( componentDescriptor.getRoleHint(), componentDescriptor );

        compositionResolver.addComponentDescriptor( componentDescriptor );
    }
}
