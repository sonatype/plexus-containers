/*
 * Created on 2003-09-21
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.codehaus.plexus.util.dag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class Vertex
{
    //------------------------------------------------------------
    //Fields
    //------------------------------------------------------------
    private String label = null;
    List adjacencyList = new ArrayList();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     *
     */
    public Vertex( final String label )
    {
        this.label = label;
    }

    // ------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------
    public List getAdjacencyList()
    {
        return adjacencyList;
    }

    /**
     * @return
     */
    public String getLabel()
    {
        return label;
    }

    /**
     *
     * @param vertex
     */
    public void addEdgeTo( final Vertex vertex )
    {
        adjacencyList.add( vertex );
    }

    /**
     * @return
     */
    public List getAdjacentLabels()
    {
        final List retValue = new ArrayList( adjacencyList.size() );
        for ( final Iterator iter = adjacencyList.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = (Vertex) iter.next();
            retValue.add( vertex.getLabel() );
        }
        return retValue;
    }
}

