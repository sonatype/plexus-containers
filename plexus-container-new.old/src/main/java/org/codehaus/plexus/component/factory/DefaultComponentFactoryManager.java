package org.codehaus.plexus.component.factory;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentFactoryManager
    implements ComponentFactoryManager
{
    private String defaultComponentFactoryId = "java";

    private List componentFactories;

    public List getComponentFactories()
    {
        return componentFactories;
    }
}
