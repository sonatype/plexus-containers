package org.codehaus.plexus.factory;

public class AbstractPlexusFactory
{
    protected static Object getInstance( String implementation, ClassLoader classLoader )
        throws Exception
    {
        return classLoader.loadClass( implementation ).newInstance();

    }
}
