package org.codehaus.plexus.component.reloading;

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

/**
 * Implementations declares whether a component should reload when
 * accessed.
 * 
 * @author Jason van Zyl
 * @version $Revision$
 */
public interface ComponentReloadingStrategy
{
    /**
     * Returns true if the given role in the container should be reloaded.
     * @param role key of the component
     * @param container the container the role lives in
     * @return true if the given role in the container should be reloaded
     * @throws ComponentReloadingException
     */
    boolean shouldReload( String role, PlexusContainer container )
        throws ComponentReloadingException;

    /**
     * Returns true if the given role/role-hint in the container should be
     * reloaded.
     * @param role key of the component
     * @param roleHint sub-key of the component
     * @param container the container the role lives in
     * @return true if the given role/role-hint in the container should be
     * reloaded
     * @throws ComponentReloadingException
     */
    boolean shouldReload( String role, String roleHint, PlexusContainer container )
        throws ComponentReloadingException;

}
