package org.codehaus.plexus.component.manager;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentManagerManager
{
    String ROLE = ComponentManagerManager.class.getName();

    ComponentManager getComponentManager( String id )
        throws UndefinedComponentManagerException;

    ComponentManager getDefaultComponentManager()
        throws UndefinedComponentManagerException;
}
