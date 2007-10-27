package org.codehaus.plexus.component.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.logging.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

/** @author Jason van Zyl */

// We need to have the collection notified when a new implementation of a given role has
// been added to the container. We probably need some options so that we know when new
// component descriptors have been added to the system, and an option to keep the collection
// up-to-date when new implementations are added.
//
// NOTE: This includes component additions, but also component purges from the
// container, as when a component realm is disposed
// (and PlexusContainer.removeComponentRealm(..) is called).

public class AbstractComponentCollection
{
    /** The reference to the PlexusContainer */
    protected PlexusContainer container;

    /** The role of the components we are holding in this Collection. */
    protected String role;

    /** The role hint of the components we are holding in this Collection. */
    protected List roleHints;

    /** The realm we need to lookup in */
    protected ClassRealm realm;

    /** The component that requires this collection of components */
    protected String hostComponent;

    /** Used to log errors in the component lookup process. */
    protected Logger logger;

    private Set lookupRealms;

    private int lastRealmCount = -1;

    private int lastComponentCount = -1;

    public AbstractComponentCollection( PlexusContainer container,
                                        ClassRealm realm,
                                        String role,
                                        List roleHints,
                                        String hostComponent )
    {
        this.container = container;

        this.realm = realm;

        this.role = role;

        this.roleHints = roleHints;

        this.hostComponent = hostComponent;

        logger = container.getLoggerManager().getLoggerForComponent( role );
    }

    /**
     * Retrieve the set of all ClassRealms with a descendant-or-self relationship
     * to the ClassRealm used to construct this component collection. This set
     * will be used to collect all of the component instances from all realms
     * which are likely to work from this collection.
     */
    protected Set getLookupRealms()
    {
        Collection allRealms = realm.getWorld().getRealms();

        if ( realmsHaveChanged() )
        {
            lookupRealms = new LinkedHashSet();

            lookupRealms.add( realm );

            int lastSize = 0;
            while( lookupRealms.size() > lastSize )
            {
                lastSize = lookupRealms.size();

                for ( Iterator it = allRealms.iterator(); it.hasNext(); )
                {
                    ClassRealm r = (ClassRealm) it.next();

                    if ( ( r.getParentRealm() != null ) && lookupRealms.contains( r.getParentRealm() ) )
                    {
                        lookupRealms.add( r );
                    }
                }
            }
        }

        return lookupRealms;
    }

    private boolean realmsHaveChanged()
    {
        return ( lookupRealms == null ) || ( realm.getWorld().getRealms().size() != lastRealmCount );
    }

    protected boolean requiresUpdate()
    {
        if ( realmsHaveChanged() )
        {
            return true;
        }

        int count = 0;
        for ( Iterator it = getLookupRealms().iterator(); it.hasNext(); )
        {
            ClassRealm r = (ClassRealm) it.next();
            count += container.getComponentDescriptorList( role, r ).size();
        }

        boolean result = count != lastComponentCount;

        lastComponentCount = count;

        return result;
    }
}
