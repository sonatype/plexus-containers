package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.Configuration;
import org.codehaus.plexus.configuration.xstream.XStreamTool;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentConfigurator
    implements ComponentConfigurator
{
    private XStreamTool xstreamTool;

    public void configureComponent( Object component, Configuration configuration )
    {

    }

    public void initialize()
        throws Exception
    {
        xstreamTool = new XStreamTool();
    }
}
