package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;

public class ComponentRepositoryFactory
    extends AbstractPlexusFactory
{
    public static ComponentRepository create( PlexusConfiguration configuration,
                                              ClassLoader classLoader )
        throws Exception
    {
        String implementation = configuration.getChild( "component-repository" ).getChild( "implementation" ).getValue();

        ComponentRepository componentRepository = (ComponentRepository) getInstance( implementation, classLoader );

        componentRepository.configure( configuration );

        componentRepository.initialize();

        return componentRepository;
    }
}
