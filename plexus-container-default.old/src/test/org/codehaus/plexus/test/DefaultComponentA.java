package org.codehaus.plexus.test;

public class DefaultComponentA
    implements ComponentA
{
    private ComponentB componentB;

    private ComponentC componentC;

    public ComponentB getComponentB()
    {
        return componentB;
    }

    public ComponentC getComponentC()
    {
        return componentC;
    }
}
