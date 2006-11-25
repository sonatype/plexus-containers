package org.codehaus.plexus.container.initialization;

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

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class ContainerInitializationContext
{
    private DefaultPlexusContainer container;

    ClassWorld classWorld;

    ClassRealm containerRealm;

    PlexusConfiguration containerConfiguration;

    public ContainerInitializationContext( DefaultPlexusContainer container,
                                           ClassWorld classWorld,
                                           ClassRealm containerRealm,
                                           PlexusConfiguration configuration )
    {
        this.container = container;
        this.classWorld = classWorld;
        this.containerRealm = containerRealm;
        this.containerConfiguration = configuration;
    }

    public DefaultPlexusContainer getContainer()
    {
        return container;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public ClassRealm getContainerRealm()
    {
        return containerRealm;
    }

    public PlexusConfiguration getContainerConfiguration()
    {
        return containerConfiguration;
    }
}
