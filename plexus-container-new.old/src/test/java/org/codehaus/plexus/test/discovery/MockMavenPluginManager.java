package org.codehaus.plexus.test.discovery;

import org.codehaus.plexus.component.discovery.ComponentDiscoveryEvent;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MockMavenPluginManager
    implements ComponentDiscoveryListener, PluginManager
{
    private List components = new ArrayList();

    public void componentDiscovered( ComponentDiscoveryEvent event )
    {
        ComponentSetDescriptor componentSetDescriptor = event.getComponentSetDescriptor();

        List discoveredComponents = componentSetDescriptor.getComponents();
        
        components.addAll( discoveredComponents );
    }

    public List getComponents()
    {
        return components;
    }
}
