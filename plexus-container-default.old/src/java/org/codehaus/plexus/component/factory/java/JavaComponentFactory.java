package org.codehaus.plexus.component.factory.java;

import org.codehaus.plexus.component.factory.AbstractComponentFactory;

public class JavaComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( String name, ClassLoader classLoader )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Object instance = classLoader.loadClass( name ).newInstance();

        return instance;
    }
}
