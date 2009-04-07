package org.codehaus.plexus;

import java.util.List;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.discovery.ComponentDiscoverer;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;

public class TestComponentDiscoverer
    implements ComponentDiscoverer
{
    public List<ComponentSetDescriptor> findComponents( Context context, ClassRealm classRealm )
        throws PlexusConfigurationException
    {
        return null;
    }
}
