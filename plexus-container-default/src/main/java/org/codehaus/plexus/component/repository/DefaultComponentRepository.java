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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

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
        LinkedHashSet<ClassRealm> realms = new LinkedHashSet<ClassRealm>();
        for (ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
            if ( classLoader instanceof ClassRealm )
            {
                ClassRealm realm = (ClassRealm) classLoader;
                realms.addAll( realm.getAccessibleRealms() );
            }
        }
        if (realms.isEmpty()) {
            realms.addAll( index.keySet() );
        }

        // Get all valid component descriptors
        Multimap<String, ComponentDescriptor<?>> roleHintIndex = Multimaps.newLinkedHashMultimap();
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
        throws ComponentRepositoryException
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
            roleHintIndex = Multimaps.newLinkedHashMultimap();
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
}
