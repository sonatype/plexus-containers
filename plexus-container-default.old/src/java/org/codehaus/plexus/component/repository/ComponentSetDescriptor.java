package org.codehaus.plexus.component.repository;

/*
 * LICENSE
 */

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentSetDescriptor
{
    /** */
    private List components;

    /** */
    private List dependencies;

    /**
     * Returns a list of {@link ComponentDescriptor}'s.
     * 
     * @return Returns a list of {@link ComponentDescriptor}'s.
     */
    public List getComponents()
    {
        return components;
    }

    /**
     * Sets a <code>List</code> of {@link ComponentDescriptor}'s.
     * 
     * @param components A list of {@link ComponentDescriptor}'s.
     */
    public void setComponents( List components )
    {
        this.components = components;
    }

    /**
     * Returns a list of {@link ComponentDependency}'s.
     * 
     * @return Returns a list of {@link ComponentDependency}'s.
     */
    public List getDependencies()
    {
        return dependencies;
    }

    /**
     * Sets a list of {@link ComponentDependency}'s.
     * 
     * @param dependencies A list of {@link ComponentDependency}'s.
     */
    public void setDependencies( List dependencies )
    {
        this.dependencies = dependencies;
    }
}
