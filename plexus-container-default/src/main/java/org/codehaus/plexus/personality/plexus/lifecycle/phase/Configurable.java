package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * Configures a component.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Configurable
{
	void configure( PlexusConfiguration configuration ) throws PlexusConfigurationException;
}
