package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.net.URL;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public interface ContainerConfiguration
{
    ContainerConfiguration setName( String name );

    String getName();

    ContainerConfiguration setContext( Map context );

    Map getContext();

    ContainerConfiguration setClassWorld( ClassWorld classWorld );

    ClassWorld getClassWorld();

    ContainerConfiguration setParentContainer( PlexusContainer container );

    PlexusContainer getParentContainer();

    ContainerConfiguration setContainerConfiguration( String configuration );

    String getContainerConfiguration();

    ContainerConfiguration setContainerConfigurationURL( URL configuration );

    URL getContainerConfigurationURL();

    ContainerConfiguration setRealm( ClassRealm realm );

    ClassRealm getRealm();

    // Programmatic Container Initialization and Setup

    String[] getInitializationPhases();

    String getComponentLookupManager();
}

