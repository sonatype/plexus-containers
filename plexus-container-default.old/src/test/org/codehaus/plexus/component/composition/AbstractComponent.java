package org.codehaus.plexus.component.composition;

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
    private ComponentA componentA;

    public ComponentA getComponentA()
    {
        return componentA;
    }
}
