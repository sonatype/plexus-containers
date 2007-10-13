package org.codehaus.plexus.component.repository.exception;

import org.codehaus.plexus.classworlds.realm.ClassRealm;

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
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentLookupException
    extends Exception
{
    private static final long serialVersionUID = 3767774496798908291L;
    private final ClassRealm realm;

    /**
     * Construct a new <code>ComponentLookupException</code> instance.
     * @param message exception message
     * @param realm
     */
    public ComponentLookupException( String message, ClassRealm realm )
    {
        super( message );
        this.realm = realm;
    }

    /**
     * Construct a new <code>ComponentLookupException</code> instance.
     * @param message exception message
     * @param cause causing exception to chain
     */
    public ComponentLookupException( String message, ClassRealm realm, Throwable cause )
    {
        super( message, cause );
        this.realm = realm;
    }

    public ClassRealm getRealm()
    {
        return realm;
    }
}
