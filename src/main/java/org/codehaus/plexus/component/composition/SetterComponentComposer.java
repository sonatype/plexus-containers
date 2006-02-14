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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class SetterComponentComposer
    extends AbstractComponentComposer
{
    public List assembleComponent( Object component,
                                   ComponentDescriptor descriptor,
                                   PlexusContainer container )
        throws CompositionException, UndefinedComponentComposerException
    {
        List requirements = descriptor.getRequirements();

        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo( component.getClass() );
        }
        catch ( IntrospectionException e )
        {
            reportErrorFailedToIntrospect( descriptor );
        }

        List retValue = new LinkedList();

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            PropertyDescriptor propertyDescriptor = findMatchingPropertyDescriptor( requirement, propertyDescriptors );

            if ( propertyDescriptor != null )
            {
                List descriptors = setProperty( component, descriptor, requirement, propertyDescriptor, container );

                retValue.addAll( descriptors );
            }
            else
            {
                reportErrorNoSuchProperty( descriptor, requirement );
            }
        }

        return retValue;
    }

    private List setProperty( Object component,
                              ComponentDescriptor descriptor,
                              ComponentRequirement requirementDescriptor,
                              PropertyDescriptor propertyDescriptor,
                              PlexusContainer container )
        throws CompositionException
    {
        Requirement requirement = CompositionUtils.findRequirement( component,
                                                                    propertyDescriptor.getPropertyType(),
                                                                    container,
                                                                    requirementDescriptor );

        try
        {
            Method writeMethod = propertyDescriptor.getWriteMethod();

            Object[] params = new Object[ 1 ];

            params[0] = requirement.getAssignment();

            Statement statement = new Statement( component, writeMethod.getName(), params );

            statement.execute();
        }
        catch ( Exception e )
        {
            reportErrorCannotAssignRequiredComponent( descriptor, requirementDescriptor, e );
        }

        return requirement.getComponentDescriptors();
    }

    protected PropertyDescriptor findMatchingPropertyDescriptor( ComponentRequirement requirement,
                                                                 PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        String property = requirement.getFieldName();

        if ( property != null )
        {
            retValue = getPropertyDescriptorByName( property, propertyDescriptors );
        }
        else
        {
            String role = requirement.getRole();

            retValue = getPropertyDescriptorByType( role, propertyDescriptors );
        }

        return retValue;
    }

    protected PropertyDescriptor getPropertyDescriptorByName( String name,
                                                              PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            if ( name.equals( propertyDescriptor.getName() ) )
            {
                retValue = propertyDescriptor;

                break;
            }
        }

        return retValue;
    }

    protected PropertyDescriptor getPropertyDescriptorByType( String type,
                                                              PropertyDescriptor[] propertyDescriptors )
    {
        PropertyDescriptor retValue = null;

        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            if ( propertyDescriptor.getPropertyType().toString().indexOf( type ) > 0 )
            {
                retValue = propertyDescriptor;

                break;
            }
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
