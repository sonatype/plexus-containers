package org.codehaus.plexus.component.discovery;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private List componentDiscoverers;

    public List getComponentDiscoverers()
    {
        return componentDiscoverers;
    }
}
