package org.codehaus.plexus.component.composition;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class SetterComponentComposer extends AbstractComponentComposer
{
    public List assembleComponent( final Object component,
                                   final ComponentDescriptor descriptor,
                                   final PlexusContainer container )
        throws CompositionException, UndefinedComponentComposerException
    {
        final List requirements = descriptor.getRequirements();

        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo( component.getClass() );
        }
        catch ( IntrospectionException e )
        {
            reportErrorFailedToIntrospect( descriptor );
        }

        final List retValue = new LinkedList();

        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for ( final Iterator i = requirements.iterator(); i.hasNext(); )
        {
            final ComponentRequirement requirement = ( ComponentRequirement ) i.next();

            final PropertyDescriptor propertyDescriptor = findMatchingPropertyDescriptor( requirement, propertyDescriptors );

            if ( propertyDescriptor != null )
            {
                final List descriptors = setProperty( component, descriptor, requirement, propertyDescriptor, container );

                retValue.addAll( descriptors );
            }
            else
            {
                reportErrorNoSuchProperty( descriptor, requirement );
            }
        }

        return retValue;
    }

    private List setProperty( final Object component,
                              final ComponentDescriptor descriptor,
                              final ComponentRequirement requirement,
                              final PropertyDescriptor propertyDescriptor,
                              final PlexusContainer container ) throws CompositionException
    {
        List retValue = null;

        final Method writeMethod = propertyDescriptor.getWriteMethod();

        final String role = requirement.getRole();

        final Object[] params = new Object[ 1 ];

        final Class propertyType = propertyDescriptor.getPropertyType();

        try
        {
            if ( propertyType.isArray() )
            {
                final Map dependencies = container.lookupMap( role );

                final Object[] array = ( Object[] ) Array.newInstance( propertyType, dependencies.size() );

                retValue = container.getComponentDescriptorList( role );

                params[ 0 ] = dependencies.entrySet().toArray( array );
            }
            else if ( Map.class.isAssignableFrom( propertyType ) )
            {
                final Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[ 0 ] = dependencies;
            }
            else if ( List.class.isAssignableFrom( propertyType ) )
            {
//                final Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[ 0 ] = container.lookupList( role );
            }
            else if ( Set.class.isAssignableFrom( propertyType ) )
            {
                final Map dependencies = container.lookupMap( role );

                retValue = container.getComponentDescriptorList( role );

                params[ 0 ] = dependencies.entrySet();
            }
            else //"ordinary" field
            {
                final String key = requirement.getRequirementKey();

                final Object dependency = container.lookup( key );

                final ComponentDescriptor componentDescriptor = container.getComponentDescriptor( key );

                retValue = new ArrayList( 1 );

                retValue.add( componentDescriptor );

                params[ 0 ] = dependency;
            }
        }
        catch ( ComponentLookupException e )
        {
            reportErrorCannotLookupRequiredComponent( descriptor, requirement, e );
        }

        final Statement statement = new Statement( component, writeMethod.getName(), params );

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

    /**
     * @param requirement
     * @return
     */
    protected PropertyDescriptor findMatchingPropertyDescriptor( final ComponentRequirement requirement,
                                                                 final PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        final String property = requirement.getFieldName();

        if ( property != null )
        {
            retValue = getPropertyDescriptorByName( property, propertyDescriptors );
        }
        else
        {
            final String role = requirement.getRole();

            retValue = getPropertyDescriptorByType( role, propertyDescriptors );
        }

        return retValue;
    }

    /**
     * @param name
     * @return
     */
    protected PropertyDescriptor getPropertyDescriptorByName( final String name,
                                                              final PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            final PropertyDescriptor propertyDescriptor = propertyDescriptors[ i ];

            if ( name.equals( propertyDescriptor.getName() ) )
            {
                retValue = propertyDescriptor;

                break;
            }
        }

        return retValue;
    }

    protected PropertyDescriptor getPropertyDescriptorByType( final String type,
                                                              final PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            final PropertyDescriptor propertyDescriptor = propertyDescriptors[ i ];

            if ( propertyDescriptor.getPropertyType().toString().indexOf( type ) > 0 )
            {
                retValue = propertyDescriptor;

                break;
            }
        }

        return retValue;
    }

    private void reportErrorNoSuchProperty( final ComponentDescriptor descriptor,
                                            final ComponentRequirement requirement ) throws CompositionException
    {

        final String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism." +
                                        " No matching property was found in bean class";

        final String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg );
    }

    private void reportErrorCannotAssignRequiredComponent( final ComponentDescriptor descriptor,
                                                           final ComponentRequirement requirement,
                                                           final Exception e ) throws CompositionException
    {
        final String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism. ";

        final String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg );
    }

    private void reportErrorCannotLookupRequiredComponent( final ComponentDescriptor descriptor,
                                                           final ComponentRequirement requirement,
                                                           final Throwable cause ) throws CompositionException
    {
        final String causeDescriprion = "Failed to lookup required component.";

        final String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg, cause );
    }

    /**
     * @param descriptor
     */
    private void reportErrorFailedToIntrospect( final ComponentDescriptor descriptor ) throws CompositionException
    {
        final String msg = getErrorMessage( descriptor, null, null );

        throw new CompositionException( msg );
    }

    private String getErrorMessage( final ComponentDescriptor descriptor,
                                    final ComponentRequirement requirement,
                                    final String causeDescription )
    {
        final StringBuffer msg = new StringBuffer( "Component composition failed." );

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
