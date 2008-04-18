package org.codehaus.plexus.aspect;

import org.codehaus.plexus.component.collections.AbstractComponentCollection;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.composition.AbstractComponentComposer;
import org.codehaus.plexus.PlexusContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is just a proof-of-concept to see whether I can keep the memory profile
 * of a container using active collections clean, particularly after a
 * component using an active collection is released.
 */
public aspect ActiveCollectionsCleanupAspect
{

    private Map PlexusContainer.collectionsByComponent = new HashMap();

    private pointcut inContainer( PlexusContainer container ):
        execution( public * PlexusContainer+.*( .. ) )
        && this( container );

    private pointcut containerStopped( PlexusContainer container ):
        execution( void PlexusContainer+.dispose() )
        && this( container );

    after( PlexusContainer container ): containerStopped( container )
    {
        for ( Iterator collectionSetIterator = container.collectionsByComponent.values().iterator(); collectionSetIterator.hasNext(); )
        {
            Set collections = (Set) collectionSetIterator.next();

            if ( collections != null )
            {
                for ( Iterator it = collections.iterator(); it.hasNext(); )
                {
                    AbstractComponentCollection collection = (AbstractComponentCollection) it.next();
                    collection.clear();
                }
            }
        }

        container.collectionsByComponent = new HashMap();
    }

    private pointcut componentMapCreation( Object hostComponent, ComponentMap collection ):
        execution( ComponentMap.new( .. ) )
        && cflow( execution( * AbstractComponentComposer.findRequirement( Object, .. ) ) )
        && args( hostComponent, .. )
        && this( collection );

    private pointcut componentMapCreationWormhole( Object hostComponent, ComponentMap collection, PlexusContainer container ):
        cflowbelow( inContainer( container ) )
        && componentMapCreation( hostComponent, collection );

    after( Object hostComponent, ComponentMap collection, PlexusContainer container ):
        componentMapCreationWormhole( hostComponent, collection, container )
    {
        Set collections = (Set) container.collectionsByComponent.get( hostComponent );
        if ( collections == null )
        {
            collections = new HashSet();
            container.collectionsByComponent.put( hostComponent, collections );
        }

        collections.add( collection );
    }

    private pointcut componentListCreation( Object hostComponent, ComponentList collection ):
        execution( ComponentList.new( .. ) )
        && cflow( execution( * AbstractComponentComposer.findRequirement( Object, .. ) ) )
        && args( hostComponent, .. )
        && this( collection );

    private pointcut componentListCreationWormhole( Object hostComponent, ComponentList collection, PlexusContainer container ):
        cflowbelow( inContainer( container ) )
        && componentListCreation( hostComponent, collection );

    after( Object hostComponent, ComponentList collection, PlexusContainer container ):
        componentListCreationWormhole( hostComponent, collection, container )
    {
        Set collections = (Set) container.collectionsByComponent.get( hostComponent );
        if ( collections == null )
        {
            collections = new HashSet();
            container.collectionsByComponent.put( hostComponent, collections );
        }

        collections.add( collection );
    }

    private pointcut activeCollectionOwnerReleased( Object component ):
        call( void ComponentManager+.release( Object ) )
        && args( component );

    private pointcut activeCollectionOwnerReleasedWormhole( Object component, PlexusContainer container ):
        cflowbelow( inContainer( container ) )
        && activeCollectionOwnerReleased( component );

    after( Object component, PlexusContainer container ):
        activeCollectionOwnerReleasedWormhole( component, container )
    {
        Set collections = (Set) container.collectionsByComponent.remove( component );
        if ( collections != null )
        {
            for ( Iterator it = collections.iterator(); it.hasNext(); )
            {
                AbstractComponentCollection collection = (AbstractComponentCollection) it.next();
                collection.clear();
            }
        }
    }
}
