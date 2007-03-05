package org.codehaus.plexus.component.composition;

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

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public interface CompositionResolver
{
    public static final char SEPARATOR_CHAR = ':';

    /**
     * @param componentDescriptor
     * @throws CompositionException when cycle is detected
     */
    void addComponentDescriptor( ComponentDescriptor componentDescriptor ) throws CompositionException;

    /**
     * Returns the list of names of components which are required
     * by the component of given role and roleHint.
     * The names returned are in the form role:hint, where : is defined in SEPARATOR_CHAR.
     *
     * @param role The name of the component
     * @param roleHint The implementation hint of the component
     * @return The list of components which are required by given component
     */
    List getRequirements( String role, String roleHint );


    /**
     * Returns the list of names of components which are using the component.
     * of given role and roleHint.
     * The names returned are in the form role:hint, where : is defined in SEPARATOR_CHAR.
     *
     * @param role The name of the component
     * @param roleHint The implementation hint of the component
     * @return The list of components which are requiring given component
     */
    List findRequirements( String role, String roleHint );


}
