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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Revision$
 */
public interface ComponentComposer
{
    static String ROLE = ComponentComposer.class.getName();

    String getId();

    void assembleComponent( Object component,
                            ComponentDescriptor componentDescriptor,
                            PlexusContainer container )
        throws CompositionException;

    void verifyComponentSuitability( Object component )
        throws CompositionException;

    Map createCompositionContext( Object component, ComponentDescriptor descriptor )
        throws CompositionException;

    List gleanAutowiringRequirements( Map compositionContext, PlexusContainer container )
        throws CompositionException;

    void assignRequirement( Object component,
                            ComponentDescriptor componentDescriptor,
                            ComponentRequirement componentRequirement,
                            PlexusContainer container, Map compositionContext )
        throws CompositionException;    
}
