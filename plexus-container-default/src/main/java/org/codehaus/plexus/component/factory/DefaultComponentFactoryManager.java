package org.codehaus.plexus.component.factory;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
