package org.codehaus.plexus.test;

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
