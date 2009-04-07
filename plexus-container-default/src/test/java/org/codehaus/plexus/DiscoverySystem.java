package org.codehaus.plexus;

import java.util.List;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.discovery.ComponentDiscovererManager;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryEvent;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;

public class DiscoverySystem
    implements ComponentDiscoverer, ComponentDiscoveryListener
{
    public List<ComponentSetDescriptor> findComponents( Context context, ClassRealm classRealm )
        throws PlexusConfigurationException
    {
        return null;
    }

    public void setManager( ComponentDiscovererManager manager )
    {
    }

    public void componentDiscovered( ComponentDiscoveryEvent event )
    {
    }

    public String getId()
    {
        return null;
    }
}
