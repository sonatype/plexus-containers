package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @ todo we should probably put the component-repository specific within the components element
 *   in the configuration so that everything can be encapsulated in one element and passed
 *   into the factory. Currently we need the whole configuration because the factory needs
 *   the component-repository element and the components element.
 */
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
