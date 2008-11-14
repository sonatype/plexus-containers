package org.codehaus.plexus.component.collections;

import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public abstract class AbstractComponentCollection
{
    /** The reference to the PlexusContainer */
    protected MutablePlexusContainer container;

    /** The role of the components we are holding in this Collection. */
    protected String role;

    /** The role hint of the components we are holding in this Collection. */
    protected List<String> roleHints;

    /** The realm we need to lookup in */
    protected ClassRealm realm;

    /** The component that requires this collection of components */
    protected String hostComponent;

    /** Used to log errors in the component lookup process. */
    protected Logger logger;

    private List<ClassRealm> realms;

    private int lastRealmCount = -1;

    private Map<String, ComponentDescriptor> componentDescriptorMap;

    public AbstractComponentCollection( MutablePlexusContainer container,
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
    protected List<ClassRealm> getLookupRealms()
    {
        Collection<ClassRealm> allRealms = new ArrayList<ClassRealm>( realm.getWorld().getRealms() );

        if ( realmsHaveChanged() )
        {
            List<ClassRealm> lookupRealms = new ArrayList<ClassRealm>();

            lookupRealms.add( realm );

            int lastSize = 0;
            while ( lookupRealms.size() > lastSize )
            {
                lastSize = lookupRealms.size();

                for ( ClassRealm realm : allRealms )
                {
                    if ( ( realm.getParentRealm() != null )
                        && lookupRealms.contains( realm.getParentRealm() )
                        && !lookupRealms.contains( realm ) )
                    {
                        lookupRealms.add( realm );
                    }
                }
            }

            realms = lookupRealms;
        }

        return realms;
    }

    protected Map<String, ClassRealm> getLookupRealmMap()
    {
        List<ClassRealm> realms = getLookupRealms();

        Map<String, ClassRealm> realmMap = new HashMap<String, ClassRealm>();
        for ( ClassRealm realm : realms )
        {
            realmMap.put( realm.getId(), realm );
        }

        return realmMap;
    }

    private boolean realmsHaveChanged()
    {
        return ( realms == null ) || ( realm.getWorld().getRealms().size() != lastRealmCount );
    }

    protected Map<String, ComponentDescriptor> getComponentDescriptorMap()
    {
        checkUpdate();

        return componentDescriptorMap;
    }

    protected boolean checkUpdate()
    {
        if ( componentDescriptorMap != null && !realmsHaveChanged() )
        {
            return false;
        }

        Map<String, ComponentDescriptor> newComponentDescriptors = new HashMap<String, ComponentDescriptor>();
        for ( ClassRealm realm : getLookupRealms() )
        {
            Map<String, ComponentDescriptor> componentMap = container.getComponentDescriptorMap( role, realm );

            if ( roleHints != null && !roleHints.isEmpty() )
            {
                for ( String roleHint : roleHints )
                {
                    ComponentDescriptor componentDescriptor = componentMap.get( roleHint );
                    if ( componentDescriptor != null )
                    {
                        newComponentDescriptors.put( roleHint, componentDescriptor );
                    }
                }
            }
            else
            {
                newComponentDescriptors.putAll( componentMap );
            }
        }

        if ( componentDescriptorMap == null || newComponentDescriptors.size() != componentDescriptorMap.size() )
        {
            componentDescriptorMap = newComponentDescriptors;

            return true;
        }

        return false;
    }

    protected Object lookup( String role,
                             String roleHint,
                             ClassRealm realm )
    {
        try
        {
            return container.lookup( role, roleHint, realm );
        }
        catch ( ComponentLookupException e )
        {
            logger.debug( "Failed to lookup a member of active collection with role: " + role
                          + " and role-hint: " + roleHint + "\nin realm: " + realm, e );

            return null;
        }
    }

    public void clear()
    {
        releaseAllCallback();

        componentDescriptorMap.clear();
        componentDescriptorMap = null;

        realms = null;
        lastRealmCount = -1;
    }

    protected abstract void releaseAllCallback();

}
