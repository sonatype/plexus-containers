package org.codehaus.plexus.test;

import org.codehaus.plexus.test.map.Activity;

public class DefaultComponent
    implements Component
{
    private String host;

    private int port;

    private Activity activity;

    public Activity getActivity()
    {
        return activity;
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
