package org.codehaus.plexus.component.composition;

import org.apache.commons.graph.domain.dependency.DependencyGraph;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class CompositionResolver
{
    private DependencyGraph componentDependencyGraph;

    public CompositionResolver()
    {
        componentDependencyGraph = new DependencyGraph();
    }

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
    {
        componentDependencyGraph.addDependencies( componentDescriptor.getComponentKey(), componentDescriptor.getRequirements() );
    }

    public List getComponentDependencies( String componentKey )
    {
        List dependencies = componentDependencyGraph.getSortedDependencies( componentKey );

        // We don't want the component key itself showing up.
        dependencies.remove( componentKey );

        return dependencies;
    }
}
