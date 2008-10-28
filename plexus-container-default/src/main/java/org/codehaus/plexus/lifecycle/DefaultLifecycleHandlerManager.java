package org.codehaus.plexus.lifecycle;

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class DefaultLifecycleHandlerManager
    implements LifecycleHandlerManager
{
    private final ConcurrentMap<String, LifecycleHandler> lifecycleHandlers = new ConcurrentHashMap<String, LifecycleHandler>();

    private String defaultLifecycleHandlerId = "plexus";

    public void addLifecycleHandler( LifecycleHandler lifecycleHandler )
    {
        lifecycleHandlers.put( lifecycleHandler.getId(), lifecycleHandler );
    }

    public void initialize()
    {
        for ( LifecycleHandler lifecycleHandler : lifecycleHandlers.values() )
        {
            lifecycleHandler.initialize();
        }
    }

    public LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException
    {
        if ( id == null )
        {
            id = defaultLifecycleHandlerId;
        }

        LifecycleHandler lifecycleHandler = lifecycleHandlers.get( id );

        if ( lifecycleHandler == null )
        {
            throw new UndefinedLifecycleHandlerException( "Specified lifecycle handler cannot be found: " + id );
        }

        return lifecycleHandler;
    }   
}
