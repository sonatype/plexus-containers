package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentConfigurator
    implements ComponentConfigurator
{
    private List decorators;

    protected PlexusConfiguration decorateConfiguration( PlexusConfiguration configuration )
    {
        return configuration;
    }
}
