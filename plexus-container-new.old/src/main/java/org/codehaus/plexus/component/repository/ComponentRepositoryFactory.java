package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.logging.LoggerManager;

public class ComponentRepositoryFactory
    extends AbstractPlexusFactory
{
    public static ComponentRepository create( Configuration defaultConfiguration,
                                              Configuration configuration,
                                              LoggerManager loggerManager,
                                              PlexusContainer container,
                                              ClassLoader classLoader,
                                              Context context )
        throws Exception
    {
        String implementation;

        if ( configuration.getChild( "component-repository", false ) != null )
        {
            implementation =
                configuration.getChild( "component-repository" ).getChild( "implementation" ).getValue();
        }
        else
        {
            implementation =
                defaultConfiguration.getChild( "component-repository" ).getChild( "implementation" ).getValue();
        }

        ComponentRepository sr = (ComponentRepository) getInstance( implementation, classLoader );

        sr.setComponentLogManager( loggerManager );
        sr.enableLogging( loggerManager.getLogger( "component-repository" ) );
        sr.contextualize( context );
        sr.setPlexusContainer( container );
        sr.configure( defaultConfiguration, configuration );
        sr.initialize();

        return sr;
    }
}
