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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class FieldComponentComposer
    extends AbstractComponentComposer
{
    public void assignRequirement( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   ComponentRequirement requirement,
                                   PlexusContainer container,
                                   Map compositionContext )
        throws CompositionException
    {
        Field field = findMatchingField( component, componentDescriptor, requirement, container );

        // we want to use private fields.
        if ( !field.isAccessible() )
        {
            field.setAccessible( true );
        }

        assignRequirementToField( component, field, container, requirement );
    }

    private List assignRequirementToField( Object component,
                                           Field field,
                                           PlexusContainer container,
                                           ComponentRequirement requirementDescriptor )
        throws CompositionException
    {
        Requirement requirement = findRequirement( component, field.getType(), container, requirementDescriptor );

        Object assignment = requirement.getAssignment();

        try
        {
            field.set( component, requirement.getAssignment() );

            return requirement.getComponentDescriptors();
        }
        catch ( IllegalArgumentException e )
        {
            System.out.println( "[" + component + ":" + ((ClassRealm) component.getClass().getClassLoader() ).getId() + "]" +
                "[" + assignment + ":" + ((ClassRealm)assignment.getClass().getClassLoader()).getId() + "]");

            throw new CompositionException( "Composition failed for the field " + field.getName() + " " +
                "in object of type " + component.getClass().getName(), e );
        }
        catch ( IllegalAccessException e )
        {
            System.out.println( "[" + component + ":" + ((ClassRealm) component.getClass().getClassLoader() ).getId() + "]" +
                "[" + assignment + ":" + ((ClassRealm)assignment.getClass().getClassLoader()).getId() + "]");

            throw new CompositionException( "Composition failed for the field " + field.getName() + " " +
                "in object of type " + component.getClass().getName(), e );
        }
    }

    protected Field findMatchingField( Object component,
                                       ComponentDescriptor componentDescriptor,
                                       ComponentRequirement requirement,
                                       PlexusContainer container )
        throws CompositionException
    {
        String fieldName = requirement.getFieldName();

        Field field = null;

        if ( fieldName != null )
        {
            field = getFieldByName( component, fieldName, componentDescriptor );
        }
        else
        {
            Class fieldClass = null;

            try
            {
                if ( container != null )
                {
                    //fieldClass = container.getContainerRealm().loadClass( requirement.getRole() );
                    
                    // Load the requirement from the same realm that the component itself comes from

                    fieldClass = component.getClass().getClassLoader().loadClass( requirement.getRole() );
                }
                else
                {
                    fieldClass = component.getClass().getClassLoader().loadClass( requirement.getRole() );
                    //fieldClass = Thread.currentThread().getContextClassLoader().loadClass( requirement.getRole() );
                }
            }
            catch ( ClassNotFoundException e )
            {
                StringBuffer msg = new StringBuffer( "Component Composition failed for component: " );

                msg.append( componentDescriptor.getHumanReadableKey() );

                msg.append( ": Requirement class: '" );

                msg.append( requirement.getRole() );

                msg.append( "' not found." );

                throw new CompositionException( msg.toString(), e );
            }

            field = getFieldByType( component, fieldClass, componentDescriptor );
        }

        return field;
    }

    protected Field getFieldByName( Object component,
                                    String fieldName,
                                    ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses( fieldName, component.getClass() );

        if ( field == null )
        {
            StringBuffer msg = new StringBuffer( "Component Composition failed. No field of name: '" );

            msg.append( fieldName );

            msg.append( "' exists in component: " );

            msg.append( componentDescriptor.getHumanReadableKey() );

            throw new CompositionException( msg.toString() );
        }

        return field;
    }

    protected Field getFieldByTypeIncludingSuperclasses( Class componentClass,
                                                         Class type,
                                                         ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        List fields = getFieldsByTypeIncludingSuperclasses( componentClass, type, componentDescriptor );

        if ( fields.size() == 0 )
        {
            return null;
        }

        if ( fields.size() == 1 )
        {
            return (Field) fields.get( 0 );
        }

        throw new CompositionException(
            "There are several fields of type '" + type + "', " + "use 'field-name' to select the correct field." );
    }

    protected List getFieldsByTypeIncludingSuperclasses( Class componentClass,
                                                         Class type,
                                                         ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Class arrayType = Array.newInstance( type, 0 ).getClass();

        Field[] fields = componentClass.getDeclaredFields();

        List foundFields = new ArrayList();

        for ( int i = 0; i < fields.length; i++ )
        {
            Class fieldType = fields[i].getType();

            if ( fieldType.isAssignableFrom( type ) || fieldType.isAssignableFrom( arrayType ) )
            {
                foundFields.add( fields[i] );
            }
        }

        if ( componentClass.getSuperclass() != Object.class )
        {
            List superFields =
                getFieldsByTypeIncludingSuperclasses( componentClass.getSuperclass(), type, componentDescriptor );

            foundFields.addAll( superFields );
        }

        return foundFields;
    }

    protected Field getFieldByType( Object component,
                                    Class type,
                                    ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        Field field = getFieldByTypeIncludingSuperclasses( component.getClass(), type, componentDescriptor );

        if ( field == null )
        {
            StringBuffer msg = new StringBuffer( "Component composition failed. No field of type: '" );

            msg.append( type );

            msg.append( "' exists in class '" );

            msg.append( component.getClass().getName() );

            msg.append( "'." );

            if ( componentDescriptor != null )
            {
                msg.append( " Component: " );

                msg.append( componentDescriptor.getHumanReadableKey() );
            }

            throw new CompositionException( msg.toString() );
        }

        return field;
    }
}
