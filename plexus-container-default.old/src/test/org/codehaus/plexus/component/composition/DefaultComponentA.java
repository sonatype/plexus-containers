package org.codehaus.plexus.component.composition;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentA
    implements ComponentA
{
    private DefaultComponentB componentB;

    // Just so we can retrieve the value of componentB for testing. */
    public DefaultComponentB getComponentB()
    {
        return componentB;
    }
}
