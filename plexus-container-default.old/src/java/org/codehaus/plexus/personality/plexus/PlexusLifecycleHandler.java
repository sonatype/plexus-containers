package org.codehaus.plexus.personality.plexus;

import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.PlexusContainer;

public class PlexusLifecycleHandler
    extends AbstractLifecycleHandler
{
    public static String COMPONENT_CONFIGURATOR = "component.configurator";

    public PlexusLifecycleHandler()
    {
        super();
    }

    public void initialize()
        throws Exception
    {
    }
}
