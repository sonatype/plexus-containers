package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentConfigurator
{
    String ROLE = ComponentConfigurator.class.getName();

    /**
     *
     * @param component
     * @param componentDescriptor conatins information which is useful for reporting problems
     *
     * @param configuration
     * @throws ComponentConfigurationException
     */
    void configureComponent( Object component,
                             ComponentDescriptor componentDescriptor,
                             PlexusConfiguration configuration )
        throws ComponentConfigurationException;
}
