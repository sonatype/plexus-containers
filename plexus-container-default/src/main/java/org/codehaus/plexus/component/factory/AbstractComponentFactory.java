package org.codehaus.plexus.component.factory;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentFactory
    implements ComponentFactory
{
    protected String id;

    public String getId()
    {
        return id;
    }
}
