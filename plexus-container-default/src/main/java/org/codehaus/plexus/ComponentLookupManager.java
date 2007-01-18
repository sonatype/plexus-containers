package org.codehaus.plexus;

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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author Kenney Westerhof
 */
public interface ComponentLookupManager
{
    String ROLE = ComponentLookupManager.class.getName();

    void setContainer( MutablePlexusContainer container );

    /**
     * @deprecated
     */
    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String componentKey, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( String role, String roleHint )
        throws ComponentLookupException;

    Object lookup( String role, String roleHint, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( Class role, String roleHint )
        throws ComponentLookupException;

    Object lookup( Class role, String roleHint, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Object lookup( Class componentClass )
        throws ComponentLookupException;

    Object lookup( Class componentClass, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Map lookupMap( String role )
        throws ComponentLookupException;

    Map lookupMap( String role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    List lookupList( String role )
        throws ComponentLookupException;

    List lookupList( String role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    List lookupList( Class role )
        throws ComponentLookupException;

    List lookupList( Class role, ClassRealm realm )
        throws ComponentLookupException;

    /**
     * @deprecated
     */
    Map lookupMap( Class role )
        throws ComponentLookupException;

    Map lookupMap( Class role, ClassRealm realm )
        throws ComponentLookupException;
}
