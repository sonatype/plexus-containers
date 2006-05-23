package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public class InitializeSystemPropertiesPhase
    extends AbstractContainerInitializationPhase
{
    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        PlexusConfiguration[] systemProperties =
            context.getContainerConfiguration().getChild( "system-properties" ).getChildren( "property" );

        for ( int i = 0; i < systemProperties.length; ++i )
        {
            String name = systemProperties[i].getAttribute( "name", null );

            String value = systemProperties[i].getAttribute( "value", null );

            if ( name == null )
            {
                throw new ContainerInitializationException( "Missing 'name' attribute in 'property' tag. " );
            }

            if ( value == null )
            {
                throw new ContainerInitializationException( "Missing 'value' attribute in 'property' tag. " );
            }

            System.getProperties().setProperty( name, value );
        }
    }
}
