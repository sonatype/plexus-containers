package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.CycleDetector;

import java.util.List;
import java.util.Set;
import java.util.Iterator;



/**
 *
 *
 * @author <a href="mailto:michal.maczka@dimatics.com">Michal Maczka</a>
 *
 * @version $Id$
 */
public class DefaultCompositionResolver implements CompositionResolver
{
    private DAG dag = new DAG();
    

    public void addComponentDescriptor( final ComponentDescriptor componentDescriptor ) throws CompositionException
    {
        final String componentKey = componentDescriptor.getComponentKey();
        final Set requirements = componentDescriptor.getRequirements();
        for ( final Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            final ComponentRequirement requirement = ( ComponentRequirement ) iterator.next();
            dag.addEdge( componentKey, requirement.getRole() );
        }
        final List cycle = CycleDetector.hasCycle( dag );
        if ( cycle!= null )
        {
             throw new  CompositionException ( CycleDetector.cycleToString( cycle ) );
        }
    }

    /**
     * 
     * @see org.codehaus.plexus.component.composition.CompositionResolver#getRequirements(java.lang.String)
     */
    public List getRequirements( final String componentKey )
    {
        return dag.getChildLabels( componentKey );        
    }
    
    
    /**
     * @see org.codehaus.plexus.component.composition.CompositionResolver#findRequirements(java.lang.String)
     */
    public List findRequirements( String componentKey )
    {        
        return dag.getParentLabels( componentKey );   
    }
}
