package org.codehaus.plexus.test;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponent
    implements Component
{
    private String host;

    private int port;

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
}
