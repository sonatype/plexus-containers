package org.codehaus.plexus.component.manager;

import java.util.Iterator;
import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentManagerManager
    implements ComponentManagerManager
{
    private List componentManagers = null;

    private String defaultComponentManagerId = null;

    public ComponentManager getComponentManager( String id )
        throws UndefinedComponentManagerException
    {
        ComponentManager componentManager = null;

        for ( Iterator iterator = componentManagers.iterator(); iterator.hasNext(); )
        {
            componentManager = (ComponentManager) iterator.next();

            if ( id.equals( componentManager.getId() ) )
            {
                return componentManager.copy();
            }
        }

        throw new UndefinedComponentManagerException( "Specified lifecycle handler cannot be found: " + id );
    }

    public ComponentManager getDefaultComponentManager()
        throws UndefinedComponentManagerException
    {
        return getComponentManager( defaultComponentManagerId ).copy();
    }
}
