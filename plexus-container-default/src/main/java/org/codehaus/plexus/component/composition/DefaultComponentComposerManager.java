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

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public class DefaultComponentComposerManager
    implements ComponentComposerManager
{
	private String defaultComponentComposerId = "field";

    private Map componentComposers;

    public void addComponentComposer( ComponentComposer componentComposer )
    {
        if ( componentComposers == null )
        {
            componentComposers = new HashMap();
        }

        componentComposers.put( componentComposer.getId(), componentComposer );
    }

    public void assembleComponent( Object component, ComponentDescriptor componentDescriptor, PlexusContainer container )
        throws UndefinedComponentComposerException, CompositionException
    {
        assembleComponent( component, componentDescriptor, container, container.getLookupRealm( component ) );
    }

    public void assembleComponent( Object component, ComponentDescriptor componentDescriptor,
                                   PlexusContainer container, ClassRealm lookupRealm )
        throws UndefinedComponentComposerException, CompositionException
    {

        if ( componentDescriptor.getRequirements().size() == 0 )
        {
            // nothing to do
            return;
        }

        String componentComposerId = componentDescriptor.getComponentComposer();

        if ( componentComposerId == null || componentComposerId.trim().length() == 0 )
        {
            componentComposerId = defaultComponentComposerId;
        }

        ComponentComposer componentComposer = getComponentComposer( componentComposerId );

        componentComposer.assembleComponent( component, componentDescriptor, container, lookupRealm );
    }

    protected ComponentComposer getComponentComposer( String id )
        throws UndefinedComponentComposerException
    {
        ComponentComposer componentComposer = (ComponentComposer) componentComposers.get( id );

        if ( componentComposer == null )
        {
            throw new UndefinedComponentComposerException( "Specified component composer cannot be found: " + id );
        }

        return componentComposer;

    }
}
