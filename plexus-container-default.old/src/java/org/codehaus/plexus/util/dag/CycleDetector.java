/*
 * Created on 2003-09-21
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.codehaus.plexus.util.dag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetector
{

    private final static Integer NOT_VISTITED = new Integer( 0 );

    private final static Integer VISITING = new Integer( 1 );

    private final static Integer VISITED = new Integer( 2 );


    public static boolean hasCycle( final DAG graph )
    {
        return dfs( graph );
    }


    private static boolean dfs( final DAG graph )
    {
        boolean hasCycle = false;
        final List verticies = graph.getVerticies();
        final Map vertexStateMap = new HashMap();
        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = ( Vertex ) iter.next();
            if ( isNotVisited( vertex, vertexStateMap ) )
            {
                hasCycle = dfsVisit( vertex, vertexStateMap );
                if ( hasCycle )
                {
                    break;
                }
            }
        }
        return hasCycle;
    }

    /**
     * @param vertex         
     * @param vertexStateMap
     * 
     * @return 
     */
    private static boolean isNotVisited( final Vertex vertex, final Map vertexStateMap )
    {
        if ( !vertexStateMap.containsKey( vertex ) )
        {
            return true;
        }
        final Integer state = ( Integer ) vertexStateMap.get( vertex );
        return NOT_VISTITED.equals( state );
    }

    /**
     * @param vertex         
     * @param vertexStateMap
     * 
     * @return 
     */
    private static boolean isVisiting( final Vertex vertex, final Map vertexStateMap )
    {
        final Integer state = ( Integer ) vertexStateMap.get( vertex );
        return VISITING.equals( state );
    }

    private static boolean dfsVisit( final Vertex vertex, final Map vertexStateMap )
    {
        vertexStateMap.put( vertex, VISITING );
        final List verticies = vertex.getAdjacencyList();
        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex v = ( Vertex ) iter.next();
            if ( isNotVisited( v, vertexStateMap ) )
            {
                dfsVisit( v, vertexStateMap );
            }
            if ( isVisiting( v, vertexStateMap ) )
            {
                return true;
            }
        }
        vertexStateMap.put( vertex, VISITED );
        return false;
    }

}