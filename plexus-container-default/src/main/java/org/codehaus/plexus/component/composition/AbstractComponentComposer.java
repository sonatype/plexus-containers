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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.lang.reflect.Array;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo just pass around a containerContext {component,descriptor,container}
 * @todo cleanup error messaging, pull out of autowire composer and generalize
 */
public abstract class AbstractComponentComposer
    extends AbstractLogEnabled
    implements ComponentComposer
{
    private String id;

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

    public List gleanAutowiringRequirements( Map compositionContext,
                                             PlexusContainer container, ClassRealm classRealm )
        throws CompositionException
    {
        return Collections.EMPTY_LIST;
    }

    public final List gleanAutowiringRequirements( Map compositionContext,
                                             PlexusContainer container )
        throws CompositionException
    {
        return Collections.EMPTY_LIST;
    }


    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
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

        List requirements;

        if ( componentDescriptor == null )
        {
            // Create a componentDescriptor to keep everything happy when we're trying to autowire.

            componentDescriptor = new ComponentDescriptor();

            componentDescriptor.setImplementation( component.getClass().getName() );

            componentDescriptor.setRole( component.getClass().getName() );

            requirements = gleanAutowiringRequirements( compositionContext, container, getRealm( component, container ) );

            componentDescriptor.addRequirements( requirements );

            try
            {
                container.addComponentDescriptor( componentDescriptor );
            }
            catch ( ComponentRepositoryException e )
            {
                // this should never happen, we never took into account creating component
                // descriptors on the fly by gleaning information.
            }
        }
        else
        {
            requirements = componentDescriptor.getRequirements();
        }

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            assignRequirement( component, componentDescriptor, requirement, container, compositionContext );
        }
    }

    public static Requirement findRequirement( Object component,
                                               Class clazz,
                                               PlexusContainer container,
                                               ComponentRequirement requirement )
        throws CompositionException
    {
        // We want to find all the requirements for a component and we want to ensure that the
        // requirements are pulled from the same realm as the component itself.

        ClassRealm componentRealm = getRealm( component, container );

        try
        {
            List componentDescriptors;

            Object assignment;

            String role = requirement.getRole();

            if ( clazz.isArray() )
            {
                List dependencies = container.lookupList( role );

                Object[] array = (Object[]) Array.newInstance( clazz, dependencies.size() );

                componentDescriptors = container.getComponentDescriptorList( role );

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
            else if ( Map.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupMap( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else if ( List.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupList( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else if ( Set.class.isAssignableFrom( clazz ) )
            {
                assignment = container.lookupMap( role );

                componentDescriptors = container.getComponentDescriptorList( role );
            }
            else
            {
                String key = requirement.getRequirementKey();

                assignment = ((MutablePlexusContainer)container).lookup( key, componentRealm );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key, componentRealm );

                componentDescriptors = new ArrayList( 1 );

                componentDescriptors.add( componentDescriptor );
            }

            return new Requirement( assignment, componentDescriptors );
        }
        catch ( ComponentLookupException e )
        {
            throw new CompositionException( "Composition failed of field " + requirement.getFieldName() + " " +
                "in object of type " + component.getClass().getName() + " because the requirement " + requirement +
                " was missing", e );
        }
    }


    protected static ClassRealm getRealm( Object component, PlexusContainer container )
    {
        if ( component.getClass().getClassLoader() instanceof ClassRealm )
        {
            return ((ClassRealm)component.getClass().getClassLoader());
        }
        else
        {
            return DefaultPlexusContainer.getLookupRealm();
        }
    }

    public String getId()
    {
        return id;
    }
}
