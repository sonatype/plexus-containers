package org.codehaus.plexus.component.composition;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentA
{
    private ComponentB componentB;

    // Just so we can retrieve the value of componentB for testing. */
    public ComponentB getComponentB()
    {
        return componentB;
    }
}
