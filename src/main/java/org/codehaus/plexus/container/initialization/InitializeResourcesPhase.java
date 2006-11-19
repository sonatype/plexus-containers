package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.net.MalformedURLException;

/**
 * @author Jason van Zyl
 */
public class InitializeResourcesPhase
    extends AbstractContainerInitializationPhase
{
    //TODO: use constants not string literals
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration[] resourceConfigs = context.getContainer().getConfiguration().getChild( "resources" ).getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                String name = resourceConfigs[i].getName();

                if ( name.equals( "jar-repository" ) )
                {
                    context.getContainer().addJarRepository( new File( resourceConfigs[i].getValue( null ) ) );
                }
                else if ( name.equals( "directory" ) )
                {
                    File directory = new File( resourceConfigs[i].getValue( null ) );

                    if ( directory.exists() && directory.isDirectory() )
                    {
                        context.getContainer().getContainerRealm().addURL( directory.toURI().toURL() );
                    }
                }
                else
                {
                    context.getContainer().getLogger().warn( "Unknown resource type: " + name );
                }
            }
            catch ( MalformedURLException e )
            {
                String message = "Error configuring resource: " + resourceConfigs[i].getName() + "=" + resourceConfigs[i].getValue( null );

                if ( context.getContainer().getLogger() != null )
                {
                    context.getContainer().getLogger().error( message, e );
                }
                else
                {
                    System.out.println( message );
                }
            }
        }
    }
}
