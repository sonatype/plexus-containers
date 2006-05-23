package org.codehaus.plexus.container.initialization;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * @author Jason van Zyl
 */
public abstract class AbstractCoreComponentInitializationPhase
    extends AbstractContainerInitializationPhase
{
    BasicComponentConfigurator configurator = new BasicComponentConfigurator();

    public void execute( ContainerInitializationContext context )
        throws ContainerInitializationException
    {
        initializeCoreComponent( context );
    }

    protected abstract void initializeCoreComponent( ContainerInitializationContext context )
        throws ContainerInitializationException;

    protected void setupCoreComponent( String role,
                                       BasicComponentConfigurator configurator,
                                       PlexusConfiguration c,
                                       PlexusContainer container )
        throws ContainerInitializationException
    {
        String implementation = c.getAttribute( "implementation", null );

        if ( implementation == null )
        {
            //TODO: put plexus.conf in constants and change to plexus.xml
            String msg = "Core component: '" + role + "' + which is needed by plexus to function properly cannot " +
                "be instantiated. Implementation attribute was not specified in plexus.conf." +
                "This is highly irregular, your plexus JAR is most likely corrupt.";

            throw new ContainerInitializationException( msg );
        }

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( role );

        componentDescriptor.setImplementation( implementation );

        PlexusConfiguration configuration = new XmlPlexusConfiguration( "containerConfiguration" );

        configuration.addChild( c );

        try
        {
            configurator.configureComponent( container, configuration, container.getContainerRealm() );
        }
        catch ( ComponentConfigurationException e )
        {
            // TODO: don't like rewrapping the same exception, but better than polluting this all through the config code
            String message = "Error configuring component: " + componentDescriptor.getHumanReadableKey();
            throw new ContainerInitializationException( message, e );
        }
    }
}
