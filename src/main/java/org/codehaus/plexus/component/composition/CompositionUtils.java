package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.Statement;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */

// a tool for determining the type of object to get from the container
// a tool for assigning
// need decent error reporting

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

                assignment = dependencies.toArray( array );
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

    private List setProperty( Object component,
                              ComponentDescriptor descriptor,
                              ComponentRequirement requirement,
                              PropertyDescriptor propertyDescriptor,
                              PlexusContainer container )
        throws CompositionException
    {
        List retValue = null;

        Method writeMethod = propertyDescriptor.getWriteMethod();

        String role = requirement.getRole();

        Object[] params = new Object[ 1 ];

        Class clazz = propertyDescriptor.getPropertyType();

        try
        {
            if ( clazz.isArray() )
            {
                Map dependencies = container.lookupMap( role );

                Object[] array = (Object[]) Array.newInstance( clazz, dependencies.size() );

                retValue = container.getComponentDescriptorList( role );

                params[0] = dependencies.entrySet().toArray( array );
            }
            else if ( Map.class.isAssignableFrom( clazz ) )
            {
                Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[0] = dependencies;
            }
            else if ( List.class.isAssignableFrom( clazz ) )
            {
//                 Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[0] = container.lookupList( role );
            }
            else if ( Set.class.isAssignableFrom( clazz ) )
            {
                Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[0] = dependencies.entrySet();
            }
            else //"ordinary" field
            {
                String key = requirement.getRequirementKey();

                Object dependency = container.lookup( key );

                ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = new ArrayList( 1 );

                retValue.add( componentDescriptor );

                params[0] = dependency;
            }
        }
        catch ( ComponentLookupException e )
        {
            reportErrorCannotLookupRequiredComponent( descriptor, requirement, e );
        }

        Statement statement = new Statement( component, writeMethod.getName(), params );

        try
        {
            statement.execute();
        }
        catch ( Exception e )
        {
            reportErrorCannotAssignRequiredComponent( descriptor, requirement, e );
        }

        return retValue;
    }

    private void reportErrorNoSuchProperty( ComponentDescriptor descriptor,
                                            ComponentRequirement requirement ) throws CompositionException
    {

        String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism." +
            " No matching property was found in bean class";

        String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg );
    }

    private void reportErrorCannotAssignRequiredComponent( ComponentDescriptor descriptor,
                                                           ComponentRequirement requirement,
                                                           Exception e ) throws CompositionException
    {
        String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism. ";

        String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg );
    }

    private void reportErrorCannotLookupRequiredComponent( ComponentDescriptor descriptor,
                                                           ComponentRequirement requirement,
                                                           Throwable cause ) throws CompositionException
    {
        String causeDescriprion = "Failed to lookup required component.";

        String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg, cause );
    }

    private void reportErrorFailedToIntrospect( ComponentDescriptor descriptor ) throws CompositionException
    {
        String msg = getErrorMessage( descriptor, null, null );

        throw new CompositionException( msg );
    }

    private String getErrorMessage( ComponentDescriptor descriptor,
                                    ComponentRequirement requirement,
                                    String causeDescription )
    {
        StringBuffer msg = new StringBuffer( "Component composition failed." );

        msg.append( "  Failed to resolve requirement for component of role: '" );

        msg.append( descriptor.getRole() );

        msg.append( "'" );

        if ( descriptor.getRoleHint() != null )
        {
            msg.append( " and role-hint: '" );

            msg.append( descriptor.getRoleHint() );

            msg.append( "'. " );
        }

        if ( requirement != null )
        {
            msg.append( "Failing requirement: " + requirement.getHumanReadableKey() );
        }
        if ( causeDescription != null )
        {
            msg.append( causeDescription );
        }

        return msg.toString();
    }
}
