package org.codehaus.plexus.component.discovery;

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

    static String COMPONENT_RESOURCES = "META-INF/plexus/components.xml";

    List findComponents( ClassLoader classLoader );
}
