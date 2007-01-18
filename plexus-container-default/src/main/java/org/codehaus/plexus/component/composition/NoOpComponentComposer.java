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
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.util.Map;

/**
 * No Op component composer. It's meant to be used with component
 * personalities which support constructor dependecy injection
 *
 * @author <a href="michal@codehaus.pl">Michal Maczka</a>
 * @version $Id$
 */
public class NoOpComponentComposer
    extends AbstractComponentComposer
{
    public String getId()
    {
        return null;
    }

    public void assignRequirement( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   ComponentRequirement requirement,
                                   PlexusContainer container, Map compositionContext,
                                   ClassRealm lookupRealm )
    {
    }
}
