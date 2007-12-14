package org.codehaus.plexus.configuration.source;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * A source for component configurations which may reside outside the configuration within a component descriptor. A
 * common usecase for this is to create a unified configuration for a set of components. For an application it is more
 * convenient to present the user with a single configuration, instead of making users work directly with a Plexus
 * configuration file which exposes component details including implementation, and wiring information.
 * 
 * @author Jason van Zyl
 */
public interface ConfigurationSource
{
    String ROLE = ConfigurationSource.class.getName();

    PlexusConfiguration getConfiguration( ComponentDescriptor componentDescriptor );
}
