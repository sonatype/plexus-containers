package org.codehaus.plexus.test;

/**
 * @component.role org.codehaus.plexus.test.ComponentA
 * @component.requirement org.codehaus.plexus.test.ComponentB
 * @component.requirement org.codehaus.plexus.test.ComponentC
 */
public class DefaultComponentA
    implements ComponentA
{
    private ComponentB componentB;

    private ComponentC componentC;

    private String host;

    private int port;

    public ComponentB getComponentB()
    {
        return componentB;
    }

    public ComponentC getComponentC()
    {
        return componentC;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
}
