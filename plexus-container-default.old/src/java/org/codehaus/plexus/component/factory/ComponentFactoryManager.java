package org.codehaus.plexus.component.factory;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentFactoryManager
{
    ComponentFactory findComponentFactory( String id )
        throws UndefinedComponentFactoryException;

    ComponentFactory getDefaultComponentFactory()
        throws UndefinedComponentFactoryException;
}
