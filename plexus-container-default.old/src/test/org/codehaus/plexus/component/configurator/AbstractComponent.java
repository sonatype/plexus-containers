package org.codehaus.plexus.component.configurator;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class AbstractComponent
    implements Component
{
    private String name;

    public String getName()
    {
        return name;
    }
}
