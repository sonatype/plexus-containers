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
    private ComponentB componentB;


    private String host;
    private String port;

    // Just so we can retrieve the value of componentB for testing. */
    public ComponentB getComponentB()
    {
        return componentB;
    }

}
