package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.logging.LoggerManager;

public class ComponentRepositoryFactory
    extends AbstractPlexusFactory
{
    public static ComponentRepository create( Configuration configuration,
                                              LoggerManager loggerManager,
                                              PlexusContainer container,
                                              ClassLoader classLoader,
                                              Context context )
        throws Exception
    {
        String implementation = configuration.getChild( "component-repository" ).getChild( "implementation" ).getValue();

        ComponentRepository componentRepository = (ComponentRepository) getInstance( implementation, classLoader );

        componentRepository.setComponentLogManager( loggerManager );

        componentRepository.enableLogging( loggerManager.getLogger( "component-repository" ) );

        componentRepository.contextualize( context );

        componentRepository.setPlexusContainer( container );

        componentRepository.configure( configuration );

        componentRepository.initialize();

        return componentRepository;
    }
}
