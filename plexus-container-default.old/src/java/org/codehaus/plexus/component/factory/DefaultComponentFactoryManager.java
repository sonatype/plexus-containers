package org.codehaus.plexus.component.factory;

import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentFactoryManager
    implements ComponentFactoryManager
{
    private String defaultComponentFactoryId = "java";

    private List componentFactories;

    public ComponentFactory findComponentFactory( String id )
        throws UndefinedComponentFactoryException
    {
        ComponentFactory componentFactory = null;

        for ( Iterator iterator = componentFactories.iterator(); iterator.hasNext(); )
        {
            componentFactory = (ComponentFactory) iterator.next();

            if ( id.equals( componentFactory.getId() ) )
            {
                return componentFactory;
            }
        }

        throw new UndefinedComponentFactoryException( "Specified component factory cannot be found: " + id );
    }

    public ComponentFactory getDefaultComponentFactory()
        throws UndefinedComponentFactoryException
    {
        return  findComponentFactory( defaultComponentFactoryId );
    }
}
