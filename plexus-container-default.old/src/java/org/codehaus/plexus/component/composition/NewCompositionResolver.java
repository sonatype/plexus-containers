package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.dag.DAG;

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
public class NewCompositionResolver implements CompositionResolver
{
    private DAG dag = new DAG();

    public List getComponentDependencies( final String componentKey )
    {
        return dag.getAdjacentLabels (componentKey );        
    }

    public void addComponentDescriptor( final ComponentDescriptor componentDescriptor )
    {
        final String componentKey = componentDescriptor.getComponentKey();        
        final Set requirements = componentDescriptor.getRequirements();        
        for ( final Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            final String requirement = ( String ) iterator.next();
            dag.addEdge( componentKey, requirement );
        }
    }
}
