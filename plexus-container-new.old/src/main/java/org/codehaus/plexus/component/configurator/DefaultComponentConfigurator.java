package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;

/**
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 *
 * @todo determine whether xstream will look in super classes for fields to set.
 */
public class DefaultComponentConfigurator
    extends AbstractComponentConfigurator
{
    private PlexusXStream xstreamTool;

    public DefaultComponentConfigurator()
    {
        xstreamTool = new PlexusXStream();
    }

    public void configureComponent( Object component,
                                    ComponentDescriptor componentDescriptor,
                                    PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        PlexusConfiguration decoratedConfiguration = decorateConfiguration( configuration );

        try
        {
            xstreamTool.build( decoratedConfiguration, component );
        }
        catch ( Exception e )
        {
            String msg = "Error configuring component "
                    + componentDescriptor.getHumanReadableKey() + ": ";

            throw new ComponentConfigurationException( msg , e );
        }
    }
}
