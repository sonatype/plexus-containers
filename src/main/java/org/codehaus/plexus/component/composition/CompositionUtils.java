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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jason van Zyl
 * @version $Id$
 */

public class CompositionUtils
{
    public static Requirement findRequirement( Object component,
                                               Class clazz,
                                               PlexusContainer container,
                                               ComponentRequirement requirement )
        throws CompositionException
    {
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
                    Iterator it = dependencies.iterator();
                    while ( it.hasNext() )
                    {
                        Class dependencyClass = it.next().getClass();
                        if ( !clazz.isAssignableFrom( dependencyClass ) )
                        {
                            throw new CompositionException( "Dependency of class " + dependencyClass.getName()
                                + " in requirement " + requirement + " is not assignable in field of class "
                                + clazz.getComponentType().getName(), e );
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

                assignment = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                componentDescriptors = new ArrayList( 1 );

                componentDescriptors.add( componentDescriptor );
            }

            return new Requirement( assignment, componentDescriptors );
        }
        catch ( ComponentLookupException e )
        {
            throw new CompositionException( "Composition failed of field " + requirement.getFieldName() + " " +
                "in object of type " + component.getClass().getName() + " because the requirement " + requirement + " was missing", e );
        }
    }
}
