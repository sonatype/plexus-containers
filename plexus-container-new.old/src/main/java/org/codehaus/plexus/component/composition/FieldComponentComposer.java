package org.codehaus.plexus.component.composition;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.ReflectionUtils;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class FieldComponentComposer
    extends AbstractComponentComposer
{
    public List assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
        throws CompositionException
    {
        List retValue = new LinkedList();

        List requirements = componentDescriptor.getRequirements();

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            Field field = findMatchingField( component, componentDescriptor, requirement, container );

            // we want to use private fields.
            if ( !field.isAccessible() )
            {
                field.setAccessible( true );
            }

            // We have a field to which we should assigning component(s).
            // Cardinality is determined by field.getType() method
            // It can be array, map, collection or "ordinary" field
            List descriptors = assignRequirementToField( component, field, container, requirement );

            retValue.addAll( descriptors );
        }

        return retValue;
    }

    private List assignRequirementToField( Object component,
                                           Field field,
                                           PlexusContainer container,
                                           ComponentRequirement requirement )
        throws CompositionException
    {
        try
        {
            List retValue;

            String role = requirement.getRole();

            if ( field.getType().isArray() )
            {
                List dependencies = container.lookupList( role );

                Object[] array = (Object[]) Array.newInstance( field.getType(), dependencies.size() );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies.toArray( array ) );
            }
            else if ( Map.class.isAssignableFrom( field.getType() ) )
            {
                Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies );
            }
            else if ( List.class.isAssignableFrom( field.getType() ) )
            {
                List dependencies = container.lookupList( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies );
            }
            else if ( Set.class.isAssignableFrom( field.getType() ) )
            {
                Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies.entrySet() );
            }
            else //"ordinary" field
            {
                String key = requirement.getRequirementKey();

                Object dependency = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = new ArrayList( 1 );

                retValue.add( componentDescriptor );

                field.set( component, dependency );
            }

            return retValue;
        }
        catch ( Exception e )
        {
            throw new CompositionException( "Composition failed: " + e.getMessage() );
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
                    fieldClass = container.getComponentRealm( requirement.getRole() ).loadClass( requirement.getRole() );
                }
                else
                {
                    fieldClass = Thread.currentThread().getContextClassLoader().loadClass( requirement.getRole() );
                }
            }
            catch ( ClassNotFoundException e )
            {
                StringBuffer msg = new StringBuffer( "Component Composition failed for component: " );

                msg.append( componentDescriptor.getHumanReadableKey() );

                msg.append( ": Requirement class: '" );

                msg.append( requirement.getRole() );

                msg.append( "' not found." );

                throw new CompositionException( msg.toString() );
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
        Field field = null;

        Class arrayType = Array.newInstance( type, 0 ).getClass();

        Field[] fields = componentClass.getDeclaredFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            Class fieldType = fields[i].getType();

            if ( fieldType.isAssignableFrom( type ) || fieldType.isAssignableFrom( arrayType ) )
            {
                field = fields[i];

                break;
            }
        }

        if ( field == null && componentClass.getSuperclass() != Object.class )
        {
            field = getFieldByTypeIncludingSuperclasses( componentClass.getSuperclass(), type, componentDescriptor );
        }

        return field;
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
