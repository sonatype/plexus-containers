package org.codehaus.plexus.component.repository;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Contains a set of ComponentDescriptors and the set's dependencies.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentSetDescriptor
{
    // ComponentDescriptor list
    private List components;

    private List dependencies;

    private boolean isolatedRealm;

    private String id;

    /**
     * Returns a list of components in this set.
     * @return a list of components
     */
    public List getComponents()
    {
        return components;
    }

    /**
     * Add a new ComponentDescriptor to this set.
     * @param cd the ComponentDescriptor to add
     */
    public void addComponentDescriptor( ComponentDescriptor cd )
    {
        if ( components == null )
        {
            components = new ArrayList();
        }

        components.add( cd );
    }

    /**
     * Sets a List of components as this set's contents.
     * @param components the List of components to set
     */
    public void setComponents( List components )
    {
        this.components = components;
    }

    /**
     * Returns a List of dependencies of this set of components.
     * @return a List of dependencies of this set of components
     */
    public List getDependencies()
    {
        return dependencies;
    }

    /**
     * Add a depenency to this set's contents.
     * @param cd the ComponentDependency to add
     */
    public void addDependency( ComponentDependency cd )
    {
        if ( dependencies == null )
        {
            dependencies = new ArrayList();
        }

        dependencies.add( cd );
    }

    /**
     * Sets a List of dependencies as this set's component dependencies.
     * @param dependencies the List of components to set
     */
    public void setDependencies( List dependencies )
    {
        this.dependencies = dependencies;
    }

    /**
     * Sets that this set of components may be in an isolated classrealm.
     * @param isolatedRealm true if this set of components may be in an
     *  isolated classrealm
     */
    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    /**
     * Returns true if this set may be in an isolated classrealm.
     * @return true if this set may be in an isolated classrealm
     */
    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    /**
     * Returns the identifier of this set.
     * @return the identifier of this set
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the identifier of this set.
     * @param id the identifier to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "Component Descriptor: " );

        for ( Iterator i = components.iterator(); i.hasNext(); )
        {
            ComponentDescriptor cd = (ComponentDescriptor) i.next();

            sb.append( cd.getHumanReadableKey() ).append( "\n" );
        }

        sb.append( "---" );

        return sb.toString();
    }
}
