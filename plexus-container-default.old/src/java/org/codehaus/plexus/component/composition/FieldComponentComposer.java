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
    public List assembleComponent( final Object component,
                                   final ComponentDescriptor componentDescriptor,
                                   final PlexusContainer container )
            throws CompositionException
    {
        final List retValue = new LinkedList();

        final Set requirements = componentDescriptor.getRequirements();

        for ( final Iterator i = requirements.iterator(); i.hasNext(); )
        {
            final ComponentRequirement requirement = ( ComponentRequirement ) i.next();

            final Field field = findMatchingField( component, componentDescriptor, requirement );

            // we want to use private fields.
            if ( !field.isAccessible() )
            {
                field.setAccessible( true );
            }

            // We have a field to which we should assigning component(s).
            // Cardinality is determined by field.getType() method
            // It can be array, map, collection or "ordinary" field
            final List descriptors = assignRequirementToField( component, field, container, requirement );

            retValue.addAll( descriptors );
        }

        return retValue;
    }

    private List assignRequirementToField( final Object component,
                                           final Field field,
                                           final PlexusContainer container,
                                           final ComponentRequirement requirement )
            throws CompositionException
    {
        try
        {
            final List retValue;

            final String role = requirement.getRole();

            if ( field.getType().isArray() )
            {
                final List dependencies = container.lookupList( role );

                final Object[] array = ( Object[] ) Array.newInstance( field.getType(), dependencies.size() );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies.toArray( array ) );
            }
            else if ( Map.class.isAssignableFrom( field.getType() ) )
            {
                final Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies );
            }
            else if ( List.class.isAssignableFrom( field.getType() ) )
            {
                final List dependencies = container.lookupList( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies );
            }
            else if ( Set.class.isAssignableFrom( field.getType() ) )
            {
                final Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                field.set( component, dependencies.entrySet() );
            }
            else //"ordinary" field
            {
                final String key = requirement.getRequirementKey();

                final Object dependency = container.lookup( key );

                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

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

    protected Field findMatchingField( final Object component,
                                       final ComponentDescriptor componentDescriptor,
                                       final ComponentRequirement requirement )
            throws CompositionException
    {
        final String fieldName = requirement.getFieldName();

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
                fieldClass = Thread.currentThread().getContextClassLoader().loadClass( requirement.getRole() );
            }
            catch ( ClassNotFoundException e )
            {
                final StringBuffer msg = new StringBuffer( "Component Composition failed for component: ");

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

    protected Field getFieldByNameIncludingSuperclasses( final Class componentClass, final String fieldName )
    {
        if ( Object.class.equals( componentClass ) )
        {
            return null;
        }

        try
        {
            final Field field = componentClass.getDeclaredField( fieldName );

            return field;
        }
        catch ( Exception e )
        {
            return getFieldByNameIncludingSuperclasses( componentClass.getSuperclass(), fieldName );
        }
    }

    protected Field getFieldByName( final Object component,
                                    final String fieldName,
                                    final ComponentDescriptor componentDescriptor )
            throws CompositionException
    {
        final Field field = getFieldByNameIncludingSuperclasses( component.getClass(), fieldName );

        if ( field == null )
        {
            final StringBuffer msg = new StringBuffer( "Component Composition failed. No field of name: '" );

            msg.append( fieldName );

            msg.append( "' exists in component: ");

            msg.append( componentDescriptor.getHumanReadableKey() );

            throw new CompositionException( msg.toString() );
        }

        return field;
    }

    protected Field getFieldByTypeIncludingSuperclasses( final Class componentClass,
                                                         final Class type,
                                                         final ComponentDescriptor componentDescriptor )
            throws CompositionException
    {
        Field field = null;

        final Class arrayType = Array.newInstance( type, 0 ).getClass();

        final Field[] fields = componentClass.getDeclaredFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            final Class fieldType = fields[ i ].getType();

            if ( fieldType.isAssignableFrom( type ) || fieldType.isAssignableFrom( arrayType ) )
            {
                field = fields[ i ];

                break;
            }
        }

        if ( field == null && componentClass.getSuperclass() != Object.class )
        {
            field = getFieldByTypeIncludingSuperclasses( componentClass.getSuperclass(), type, componentDescriptor );
        }

        return field;
    }

    protected Field getFieldByType( final Object component,
                                    final Class type,
                                    final ComponentDescriptor componentDescriptor )
            throws CompositionException
    {
        final Field field = getFieldByTypeIncludingSuperclasses( component.getClass(), type, componentDescriptor );

        if ( field == null )
        {
            final StringBuffer msg = new StringBuffer( "Component composition failed. No field of type: '" );

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
