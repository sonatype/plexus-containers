package org.codehaus.plexus.personality.plexus.lifecycle.phase;

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
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Map;

/**
 * A ServiceLocator for PlexusContainer.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusContainerLocator
    implements ServiceLocator
{
    private PlexusContainer container;

    public PlexusContainerLocator( PlexusContainer container )
    {
        this.container = container;
    }

    public Object lookup( String componentKey )
        throws ComponentLookupException
    {
        return container.lookup( componentKey );
    }

    public Object lookup( String role,
                          String roleHint )
        throws ComponentLookupException
    {
        return container.lookup( role, roleHint );
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        return container.lookupMap( role );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        return container.lookupList( role );
    }

    public void release( Object component )
        throws ComponentLifecycleException
    {
        container.release( component );
    }

    public void releaseAll( Map components )
        throws ComponentLifecycleException
    {
        container.releaseAll( components );
    }

    public void releaseAll( List components )
        throws ComponentLifecycleException
    {
        container.releaseAll( components );
    }

    public boolean hasComponent( String componentKey )
    {
        return container.hasComponent( componentKey );
    }

    public boolean hasComponent( String role,
                                 String roleHint )
    {
        return container.hasComponent( role, roleHint );
    }
}
