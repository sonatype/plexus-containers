package org.codehaus.plexus.service.repository.factory;

public class JavaComponentFactory
    implements ComponentFactory
{
    public Object newInstance( String name, ClassLoader classLoader )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Object instance = classLoader.loadClass( name ).newInstance();

        return instance;
    }
}
