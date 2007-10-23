package org.codehaus.plexus.component.composition;

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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl
 * @version $Id$
 * @todo just pass around a containerContext {component,descriptor,container}
 * @todo cleanup error messaging, pull out of autowire composer and generalize
 */
public abstract class AbstractComponentComposer
    extends AbstractLogEnabled
    implements ComponentComposer
{
    // ----------------------------------------------------------------------
    // Composition Life Cycle
    // ----------------------------------------------------------------------

    public void verifyComponentSuitability( Object component )
        throws CompositionException
    {
    }

    public Map createCompositionContext( Object component,
                                         ComponentDescriptor descriptor )
        throws CompositionException
    {
        return Collections.EMPTY_MAP;
    }

    /**
     * @deprecated
     */
    public void assembleComponent( Object component, ComponentDescriptor componentDescriptor, PlexusContainer container )
        throws CompositionException
    {
        assembleComponent( component, componentDescriptor, container, container.getLookupRealm( component ) );
    }

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container, ClassRealm lookupRealm )
        throws CompositionException
    {
        // ----------------------------------------------------------------------
        // If a ComponentComposer wishes to check any criteria before attempting
        // composition it may do so here.
        // ----------------------------------------------------------------------

        verifyComponentSuitability( component );

        Map compositionContext = createCompositionContext( component, componentDescriptor );

        // ----------------------------------------------------------------------
        // If the ComponentDescriptor is null then we are attempting to autowire
        // this component which means that Plexus has just been handled a
        // POJO to wire up.
        // ----------------------------------------------------------------------

        List requirements = componentDescriptor.getRequirements();

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            assignRequirement( component, componentDescriptor, requirement, container, compositionContext, lookupRealm );
        }
    }

    /**
     * @deprecated
     */
    public final void assignRequirement( Object component, ComponentDescriptor componentDescriptor,
                                   ComponentRequirement componentRequirement, PlexusContainer container,
                                   Map compositionContext )
        throws CompositionException
    {
        assignRequirement( component,
                           componentDescriptor,
                           componentRequirement,
                           container,
                           compositionContext,
                           container.getLookupRealm( component ) );
    }


    public static Requirement findRequirement( Object component,
                                               Class clazz,
                                               PlexusContainer container,
                                               ComponentRequirement requirement,
                                               ClassRealm lookupRealm )
        throws CompositionException
    {
        // We want to find all the requirements for a component and we want to ensure that the
        // requirements are pulled from the same realm as the component itself.

        try
        {
            List componentDescriptors;

            Object assignment;

            String role = requirement.getRole();

            List roleHints = null;

            if ( requirement instanceof ComponentRequirementList )
            {
                roleHints = ( (ComponentRequirementList) requirement ).getRoleHints();
            }

            if ( clazz.isArray() )
            {
                List dependencies = container.lookupList( role, roleHints, lookupRealm );

                Object[] array = (Object[]) Array.newInstance( clazz, dependencies.size() );

                componentDescriptors = container.getComponentDescriptorList( role, lookupRealm );

                try
                {
                    assignment = dependencies.toArray( array );
                }
                catch ( ArrayStoreException e )
                {
                    for ( Iterator i = dependencies.iterator(); i.hasNext(); )
                    {
                        Class dependencyClass = i.next().getClass();

                        if ( !clazz.isAssignableFrom( dependencyClass ) )
                        {
                            throw new CompositionException( "Dependency of class " + dependencyClass.getName() +
                                " in requirement " + requirement + " is not assignable in field of class " +
                                clazz.getComponentType().getName(), e );
                        }
                    }

                    // never gets here
                    throw e;
                }
            }
            // Map.class.isAssignableFrom( clazz ) doesn't make sense, since Map.class doesn't really
            // have a meaningful superclass.
            else if ( Map.class.equals( clazz ) )
            {
                //assignment = container.lookupMap( role, roleHints, lookupRealm );

                assignment = new ComponentMap( container, lookupRealm, role, roleHints );

                componentDescriptors = container.getComponentDescriptorList( role, lookupRealm );
            }
            // List.class.isAssignableFrom( clazz ) doesn't make sense, since List.class doesn't really
            // have a meaningful superclass other than Collection.class, which we'll handle next.
            else if ( List.class.equals( clazz ) )
            {                    
                //assignment = container.lookupList( role, lookupRealm );

                assignment = new ComponentList( container, lookupRealm, role, roleHints );

                componentDescriptors = container.getComponentDescriptorList( role, lookupRealm );
            }
            // Set.class.isAssignableFrom( clazz ) doesn't make sense, since Set.class doesn't really
            // have a meaningful superclass other than Collection.class, and that would make this
            // if-else cascade unpredictable (both List and Set extend Collection, so we'll put another
            // check in for Collection.class.
            else if ( Set.class.equals( clazz ) || Collection.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupMap( role, roleHints, lookupRealm );

                componentDescriptors = container.getComponentDescriptorList( role, lookupRealm );
            }
            else
            {
                String roleHint = requirement.getRoleHint();

                assignment = container.lookup( role, roleHint, lookupRealm );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( role, roleHint, lookupRealm );

                componentDescriptors = new ArrayList( 1 );

                componentDescriptors.add( componentDescriptor );
            }

            return new Requirement( assignment, componentDescriptors );
        }
        catch ( ComponentLookupException e )
        {
            throw new CompositionException( "Composition failed of field " + requirement.getFieldName() + " " +
                "in object of type " + component.getClass().getName() + " because the requirement " + requirement +
                " was missing (lookup realm: " + lookupRealm.getId() + ")", e );
        }
    }
}
