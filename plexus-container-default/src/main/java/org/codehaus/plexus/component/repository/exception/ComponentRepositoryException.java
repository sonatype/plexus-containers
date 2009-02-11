package org.codehaus.plexus.component.repository.exception;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

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
 * Exception that is thrown when the class(es) required for a component
 * implementation are not available.
 *
 * @author Jason van Zyl
 * @version $Id$
 */
public class ComponentRepositoryException
    extends Exception
{
    private static final long serialVersionUID = 3698017788731736736L;

    private String LS = System.getProperty( "line.separator" );

    private String role;

    private String roleHint;

    private ClassRealm realm;

    public ComponentRepositoryException( String message, String role, String roleHint )
    {
        super( message );

        this.role = role;

        this.roleHint = roleHint;
    }

    public ComponentRepositoryException( String message, Class<?> type, String roleHint )
    {
        super( message );

        this.role = type.getName();

        this.roleHint = roleHint;
    }

    public ComponentRepositoryException( String message, String role, String roleHint, Throwable cause )
    {
        super( message, cause );

        this.role = role;

        this.roleHint = roleHint;
    }

    public ComponentRepositoryException( String message, String role, String roleHint, ClassRealm realm )
    {
        super( message );

        this.role = role;

        this.roleHint = roleHint;

        this.realm = realm;
    }

    public ComponentRepositoryException( String message, String role, String roleHint, ClassRealm realm, Throwable cause )
    {
        super( message, cause );

        this.role = role;

        this.roleHint = roleHint;

        this.realm = realm;
    }

    public ComponentRepositoryException( String message, Class<?> type, String roleHint, ClassRealm realm )
    {
        super( message );

        this.role = type.getName();

        this.roleHint = roleHint;

        this.realm = realm;
    }

    public ComponentRepositoryException( String message, ComponentDescriptor<?> descriptor )
    {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm() );
    }

    public ComponentRepositoryException( String message, ComponentDescriptor<?> descriptor, Throwable cause )
    {
        this( message, descriptor.getRole(), descriptor.getRoleHint(), descriptor.getRealm(), cause );
    }

    public String getMessage()
    {
        StringBuffer sb = new StringBuffer()
            .append( super.getMessage() ).append( LS )
            .append( "      role: " ).append( role ).append( LS )
            .append( "  roleHint: " ).append( roleHint ).append( LS )
            .append( "classRealm: " );

        if ( realm != null )
        {
            sb.append( realm.getId() );
            realm.display();
        }
        else
        {
            sb.append( "none specified" );
        }

        return sb.toString();
    }
}
