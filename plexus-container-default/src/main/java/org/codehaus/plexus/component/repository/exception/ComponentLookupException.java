package org.codehaus.plexus.component.repository.exception;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

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

/**
 * The exception which is thrown by a component repository when
 * the requested component cannot be found.
 *
 * @author Jason van Zyl
 * @version $Id$
 */
public class ComponentLookupException
    extends Exception
{
    private String LS = System.getProperty( "line.separator" );

    private String role;

    private String roleHint;

    private ClassRealm realm;

    private List<ComponentDescriptor<?>> componentStack = new ArrayList<ComponentDescriptor<?>>();

    public ComponentLookupException( String message, String role, String roleHint )
    {
        this( message, role, roleHint, null, null, null);
    }

    public ComponentLookupException( String message, Class<?> type, String roleHint )
    {
        this( message, type.getName(), roleHint, null, null, null);
    }

    public ComponentLookupException( String message, Class<?> type, String roleHint, Collection<ComponentDescriptor<?>> componentStack )
    {
        this( message, type.getName(), roleHint, null, componentStack, null);
    }

    public ComponentLookupException( String message, String role, String roleHint, Throwable cause )
    {
        this( message, role, roleHint, null, null, cause);
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm )
    {
        this( message, role, roleHint, realm, null, null );
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm,  Collection<ComponentDescriptor<?>> componentStack )
    {
        this( message, role, roleHint, realm, componentStack, null );
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm, Throwable cause )
    {
        this( message, role, roleHint, realm, null, cause );
    }

    public ComponentLookupException( String message, String role, String roleHint, ClassRealm realm, Collection<ComponentDescriptor<?>> componentStack, Throwable cause )
    {
        super( message, cause );
        this.role = role;
        this.roleHint = roleHint;
        this.realm = realm;
        if ( componentStack != null )
        {
            this.componentStack.addAll( componentStack );
        }
    }

    public ComponentLookupException( String message, Class<?> type, String roleHint, ClassRealm realm )
    {
        this( message, type.getName(), roleHint, realm, null, null);
    }

    public ComponentLookupException( String message, ComponentDescriptor<?> descriptor ) {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm(), null, null );
    }

    public ComponentLookupException( String message, ComponentDescriptor<?> descriptor,  Collection<ComponentDescriptor<?>> componentStack ) {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm(), componentStack, null );
    }

    public ComponentLookupException( String message, ComponentDescriptor<?> descriptor, Throwable cause ) {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm(), null, cause );
    }

    public ComponentLookupException( String message, ComponentDescriptor<?> descriptor,  Collection<ComponentDescriptor<?>> componentStack, Throwable cause ) {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm(), componentStack, cause );
    }

    public List<ComponentDescriptor<?>> getComponentStack()
    {
        return Collections.unmodifiableList( componentStack );
    }

    public void setComponentStack( List<ComponentDescriptor<?>> componentStack )
    {
        this.componentStack.clear();
        this.componentStack.addAll( componentStack );
    }

    public String getMessage()
    {
        StringBuffer sb = new StringBuffer()
            .append( super.getMessage() ).append( LS )
            .append( "      role: ").append( role ).append( LS )
            .append( "  roleHint: ").append( roleHint ).append( LS )
            .append( "classRealm: ");

        if ( realm != null )
        {
            sb.append( realm.getId() );
            realm.display();
        }
        else
        {
            sb.append( "none specified" );
        }
        sb.append( LS );

        sb.append( LS );
        sb.append( "Component stack:" ).append( LS );
        for ( ComponentDescriptor<?> descriptor : componentStack )
        {
            sb.append( '\t' ).append( descriptor.getHumanReadableKey() ).append( LS );
        }

        sb.append( LS );
        sb.append( "Code stack:" );
        return sb.toString();
    }
}
