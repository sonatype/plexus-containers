package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xstream.XStreamTool;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentConfigurator
    implements ComponentConfigurator
{
    private XStreamTool xstreamTool;

    public DefaultComponentConfigurator()
    {
        xstreamTool = new XStreamTool();
    }

    public void configureComponent( Object component, PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        try
        {
            xstreamTool.build( configuration, component );
        }
        catch ( Exception e )
        {
            throw new ComponentConfigurationException( "Error configuring component: ", e );
        }
    }
}
