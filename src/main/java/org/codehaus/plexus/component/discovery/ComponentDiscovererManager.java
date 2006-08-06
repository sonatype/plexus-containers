package org.codehaus.plexus.component.discovery;

import java.util.List;

public interface ComponentDiscovererManager
{
    List getComponentDiscoverers();

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener);

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event );

    void initialize();

    List getListeners();
}
