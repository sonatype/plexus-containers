package org.codehaus.plexus.component.composition.setter;

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
import org.codehaus.plexus.component.composition.AbstractComponentComposer;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.Requirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.Statement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class SetterComponentComposer
    extends AbstractComponentComposer
{
    public static final String PROPERTY_DESCRIPTORS = SetterComponentComposer.class.getName() + ":property.descriptors";

    public Map createCompositionContext( Object component,
                                         ComponentDescriptor descriptor )
        throws CompositionException
    {
        Map compositionContext = new HashMap();

        BeanInfo beanInfo = null;

        try
        {
            beanInfo = Introspector.getBeanInfo( component.getClass() );
        }
        catch ( IntrospectionException e )
        {
            throw new CompositionException( getErrorMessage( descriptor, null, null ) );
        }

        compositionContext.put( PROPERTY_DESCRIPTORS, beanInfo.getPropertyDescriptors() );

        return compositionContext;
    }

    public void assignRequirement( Object component,
                                   ComponentDescriptor descriptor,
                                   ComponentRequirement requirement,
                                   PlexusContainer container,
                                   Map compositionContext,
                                   ClassRealm lookupRealm )
        throws CompositionException
    {
        PropertyDescriptor[] propertyDescriptors =
            (PropertyDescriptor[]) compositionContext.get( PROPERTY_DESCRIPTORS );

        PropertyDescriptor propertyDescriptor = findMatchingPropertyDescriptor( requirement, propertyDescriptors );

        if ( propertyDescriptor != null )
        {
            setProperty( component, descriptor, requirement, propertyDescriptor, container, lookupRealm );
        }
        else
        {
            reportErrorNoSuchProperty( descriptor, requirement );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public List gleanAutowiringRequirements( Map compositionContext,
                                             PlexusContainer container, ClassRealm componentRealm )
    {
        PropertyDescriptor[] propertyDescriptors =
            (PropertyDescriptor[]) compositionContext.get( PROPERTY_DESCRIPTORS );

        List requirements = new ArrayList();

        for ( int i = 0; i < propertyDescriptors.length; i++ )
        {
            PropertyDescriptor pd = propertyDescriptors[i];

            String role = pd.getPropertyType().getName();

            ComponentDescriptor componentDescriptor = container.getComponentDescriptor( role, componentRealm );

            if ( componentDescriptor != null )
            {
                ComponentRequirement requirement = new ComponentRequirement();

                requirement.setRole( role );

                requirements.add( requirement );
            }
        }

        return requirements;
    }


    private List setProperty( Object component,
                              ComponentDescriptor descriptor,
                              ComponentRequirement requirementDescriptor,
                              PropertyDescriptor propertyDescriptor,
                              PlexusContainer container, ClassRealm lookupRealm )
        throws CompositionException
    {
        Requirement requirement = findRequirement( component,
                                                   propertyDescriptor.getPropertyType(),
                                                   container,
                                                   requirementDescriptor,
                                                   lookupRealm );

        try
        {
            Method writeMethod = propertyDescriptor.getWriteMethod();

            Object[] params = new Object[1];

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
                                            ComponentRequirement requirement )
        throws CompositionException
    {

        String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism." +
            " No matching property was found in bean class";

        String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

        throw new CompositionException( msg );
    }

    private void reportErrorCannotAssignRequiredComponent( ComponentDescriptor descriptor,
                                                           ComponentRequirement requirement,
                                                           Exception e )
        throws CompositionException
    {
        String causeDescriprion = "Failed to assign requirment using Java Bean introspection mechanism. ";

        String msg = getErrorMessage( descriptor, requirement, causeDescriprion );

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
