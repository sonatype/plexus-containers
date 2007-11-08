package org.codehaus.plexus.aspect;

import org.codehaus.plexus.component.collections.AbstractComponentCollection;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.composition.AbstractComponentComposer;

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

    private Map collectionsByComponent = new HashMap();

    private pointcut componentMapCreation( Object hostComponent, ComponentMap collection ):
        execution( ComponentMap.new( .. ) )
        && cflow( execution( * AbstractComponentComposer.findRequirement( Object, .. ) ) )
        && args( hostComponent, .. )
        && this( collection );

    after( Object hostComponent, ComponentMap collection ):
        componentMapCreation( hostComponent, collection )
    {
        Set collections = (Set) collectionsByComponent.get( hostComponent );
        if ( collections == null )
        {
            collections = new HashSet();
            collectionsByComponent.put( hostComponent, collections );
        }

        collections.add( collection );
    }

    private pointcut componentListCreation( Object hostComponent, ComponentList collection ):
        execution( ComponentList.new( .. ) )
        && cflow( execution( * AbstractComponentComposer.findRequirement( Object, .. ) ) )
        && args( hostComponent, .. )
        && this( collection );

    after( Object hostComponent, ComponentList collection ):
        componentListCreation( hostComponent, collection )
    {
        Set collections = (Set) collectionsByComponent.get( hostComponent );
        if ( collections == null )
        {
            collections = new HashSet();
            collectionsByComponent.put( hostComponent, collections );
        }

        collections.add( collection );
    }

    private pointcut activeCollectionOwnerReleased( Object component ):
        call( void ComponentManager+.release( Object ) )
        && args( component );

    after( Object component ): activeCollectionOwnerReleased( component )
    {
        Set collections = (Set) collectionsByComponent.remove( component );
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
