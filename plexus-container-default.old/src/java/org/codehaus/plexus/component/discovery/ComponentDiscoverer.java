package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ComponentDiscoverer
{
    static String ROLE = ComponentDiscoverer.class.getName();

    List findComponents( ClassLoader classLoader );

    String getComponentDescriptorLocation();

    List createComponentDescriptors( PlexusConfiguration componentDescriptorConfiguration )
        throws Exception;
}
