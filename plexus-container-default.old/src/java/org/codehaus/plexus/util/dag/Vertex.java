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
public class Vertex implements Cloneable
{
    //------------------------------------------------------------
    //Fields
    //------------------------------------------------------------
    private String label = null;
    List children = new ArrayList();
    List parents = new ArrayList();
    
    
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
        children.add( vertex );       
    }
    
    
    /**
     * @param vertex
     */
    public void addEdgeFrom( final Vertex vertex )
    {
       parents.add(  vertex );        
    }

    public List getChildren()
    {
        return children;
    }
    
    
    /**
     * Get the labels used by the most direct children. 
     * @return the labels used by the most direct children.
     */
    public List getChildLabels()
    {
        final List retValue = new ArrayList( children.size() );
        for ( final Iterator iter = children.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = (Vertex) iter.next();
            retValue.add( vertex.getLabel() );
        }
        return retValue;
    }
    
    
    /**
     * Get the list the most direct ancestors (parents).
     * @return list of parents 
     */
    public List getParents()
    {
        return parents;
    }
    
    
    /**
     * Get the labels used by the most direct ancestors (parents). 
     * @return the labels used parents 
     */
    public List getParentLabels()
    {
        final List retValue = new ArrayList( parents.size() );
        for ( final Iterator iter = parents.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = (Vertex) iter.next();
            retValue.add( vertex.getLabel() );
        }
        return retValue;
    }
    
    public boolean isLeaf()
    {
       return children.size() == 0;    
    }
    
    
    public Object clone() throws CloneNotSupportedException 
    {         
        Object o = super.clone();	// this is what's failing..               
        return o;
    }

   
}

