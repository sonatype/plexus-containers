package org.codehaus.plexus.test;

public class DefaultComponentA
    implements ComponentA
{
    private DefaultComponentB componentB;

    public ComponentB getComponentB()
    {
        return componentB;
    }
}
