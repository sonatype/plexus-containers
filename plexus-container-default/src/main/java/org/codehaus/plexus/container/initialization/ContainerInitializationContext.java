package org.codehaus.plexus.container.initialization;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class ContainerInitializationContext
{
    private DefaultPlexusContainer container;

    ClassWorld classWorld;

    ClassRealm containerRealm;

    PlexusConfiguration containerConfiguration;

    public ContainerInitializationContext( DefaultPlexusContainer container,
                                           ClassWorld classWorld,
                                           ClassRealm containerRealm,
                                           PlexusConfiguration configuration )
    {
        this.container = container;
        this.classWorld = classWorld;
        this.containerRealm = containerRealm;
        this.containerConfiguration = configuration;
    }

    public DefaultPlexusContainer getContainer()
    {
        return container;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public ClassRealm getContainerRealm()
    {
        return containerRealm;
    }

    public PlexusConfiguration getContainerConfiguration()
    {
        return containerConfiguration;
    }
}
