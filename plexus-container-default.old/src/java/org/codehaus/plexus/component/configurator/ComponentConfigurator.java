package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentConfigurator
{
    void configureComponent( Object component, PlexusConfiguration configuration )
        throws ComponentConfigurationException;
}
