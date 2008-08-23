package org.codehaus.plexus.component;

public interface Hello
{

    final static String ROLE = Hello.class.getName();

    void sayHello();
}