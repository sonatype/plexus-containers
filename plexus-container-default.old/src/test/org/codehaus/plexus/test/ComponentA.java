package org.codehaus.plexus.test;

public interface ComponentA
{
    static String ROLE = ComponentA.class.getName();

    ComponentB getComponentB();

    ComponentC getComponentC();

    String getHost();

    int getPort();
}
