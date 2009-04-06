package org.codehaus.plexus.component.discovery;

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

import java.util.List;
import java.util.Map;

/**
 * ComponentDiscovererManager is a simple index (registry) of ComponentDiscovers and ComponentDiscoveryListener.
 */
public interface ComponentDiscovererManager
{
    void addComponentDiscoverer( ComponentDiscoverer componentDiscoverer );

    List<ComponentDiscoverer> getComponentDiscoverers();

    // todo dain change this to Set<ComponentDiscoveryListener> (requires change to maven)
    Map<ComponentDiscoveryListener, Object> getComponentDiscoveryListeners();

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener);

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event );
}
