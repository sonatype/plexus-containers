package org.codehaus.plexus.component.repository;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentSet
{
    private List components;

    private List dependencies;

    public List getComponents()
    {
        return components;
    }

    public void setComponents( List components )
    {
        this.components = components;
    }

    public List getDependencies()
    {
        return dependencies;
    }

    public void setDependencies( List dependencies )
    {
        this.dependencies = dependencies;
    }
}
