package org.codehaus.plexus;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.net.URL;
import java.util.Map;

/**
 * @author Jason van Zyl
 */
public class DefaultContainerConfiguration
    implements ContainerConfiguration
{
    private String name;

    private Map context;

    private ClassWorld classWorld;

    private ClassRealm realm;

    private PlexusContainer parentContainer;

    private String containerConfiguration;

    public ContainerConfiguration setName( String name )
    {
        this.name = name;

        return this;
    }

    public ContainerConfiguration setContext( Map context )
    {
        this.context = context;

        return this;
    }

    public ContainerConfiguration setClassWorld( ClassWorld classWorld )
    {
        this.classWorld = classWorld;

        return this;
    }

    public ContainerConfiguration setRealm( ClassRealm realm )
    {
        this.realm = realm;

        return this;
    }

    public ContainerConfiguration setParentContainer( PlexusContainer parentContainer )
    {
        this.parentContainer = parentContainer;

        return this;
    }

    public ContainerConfiguration setContainerConfiguration( String containerConfiguration )
    {
        this.containerConfiguration = containerConfiguration;

        return this;
    }

    public String getContainerConfiguration()
    {
        return containerConfiguration;
    }

    public String getName()
    {
        return name;
    }

    public Map getContext()
    {
        return context;
    }

    public ClassWorld getClassWorld()
    {
        return classWorld;
    }

    public PlexusContainer getParentContainer()
    {
        return parentContainer;
    }

    public ClassRealm getRealm()
    {
        return realm;
    }
}
