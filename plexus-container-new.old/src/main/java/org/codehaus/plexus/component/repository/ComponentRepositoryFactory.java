package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.factory.AbstractPlexusFactory;

public class ComponentRepositoryFactory
    extends AbstractPlexusFactory
{
    public static ComponentRepository create( Configuration configuration,
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
