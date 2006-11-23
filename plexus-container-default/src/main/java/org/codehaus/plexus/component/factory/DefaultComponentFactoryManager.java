package org.codehaus.plexus.component.factory;

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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.StringUtils;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentFactoryManager
    implements ComponentFactoryManager, Contextualizable
{
    private String defaultComponentFactoryId = "java";

    private ComponentFactory defaultComponentFactory = new JavaComponentFactory();

    private PlexusContainer container;
    
    /** @deprecated Register factories as components with language as role-hint instead.*/
    private List componentFactories;

    public ComponentFactory findComponentFactory( String id )
        throws UndefinedComponentFactoryException
    {
        if(StringUtils.isEmpty(id) || defaultComponentFactoryId.equals(id))
        {
            return defaultComponentFactory;
        }
        else
        {
            try
            {
                return (ComponentFactory) container.lookup(ComponentFactory.ROLE, id);
            }
            catch ( ComponentLookupException e )
            {
                throw new UndefinedComponentFactoryException( "Specified component factory cannot be found: " + id, e );
            }
        }

        // Commented out until we get active collections working; we'll do direct
        // lookups until then.
//        for ( Iterator iterator = componentFactories.iterator(); iterator.hasNext(); )
//        {
//            componentFactory = (ComponentFactory) iterator.next();
//
//            if ( id.equals( componentFactory.getId() ) )
//            {
//                return componentFactory;
//            }
//        }

    }

    public ComponentFactory getDefaultComponentFactory()
        throws UndefinedComponentFactoryException
    {
        return  defaultComponentFactory;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
