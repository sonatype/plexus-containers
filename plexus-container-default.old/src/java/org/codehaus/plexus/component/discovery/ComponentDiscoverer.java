package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.List;
import java.io.Reader;

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

    String getComponentType();

    List createComponentDescriptors( Reader reader, String source )
        throws Exception;

    void setManager( ComponentDiscovererManager manager );
}
