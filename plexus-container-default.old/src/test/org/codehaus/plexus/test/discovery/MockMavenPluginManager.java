package org.codehaus.plexus.test.discovery;

import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryEvent;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class MockMavenPluginManager
    implements ComponentDiscoveryListener, PluginManager
{
    boolean discoveryEventRegistered = false;

    public void componentDiscovered( ComponentDiscoveryEvent event )
    {
        if ( event.getComponentType().equals( "maven-plugin" ) )
        {
            discoveryEventRegistered = true;
        }
    }

    public boolean getDiscoveryEventRegistered()
    {
        return discoveryEventRegistered;
    }
}