package org.codehaus.plexus.component.discovery;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ComponentDiscoverer
{
    static String ROLE = ComponentDiscoverer.class.getName();

    void setManager( ComponentDiscovererManager manager );

    List findComponents( ClassLoader classLoader );
}
