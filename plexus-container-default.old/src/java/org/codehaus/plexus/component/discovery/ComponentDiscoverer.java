package org.codehaus.plexus.component.discovery;

import java.io.Reader;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

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

    void setManager( ComponentDiscovererManager manager );

    ComponentSetDescriptor findComponents( ClassLoader classLoader );
}
