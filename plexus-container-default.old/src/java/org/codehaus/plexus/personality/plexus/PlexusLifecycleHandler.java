package org.codehaus.plexus.personality.plexus;

import org.codehaus.plexus.lifecycle.AbstractLifecycleHandler;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;


/** An Avalon component lifecycle handler.
 *
 *  The <code>AvalonLifecycleHandler</code> must have the following entities
 *  set in order to propery execute the Avalon lifecycle.
 *
 *  Logger
 *  Context
 *  ServiceManager
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 *
 *  @todo need suspendSegment/resumeSegment facilities.
 */
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
        ComponentConfigurator componentConfigurator = (ComponentConfigurator) getEntities().get( COMPONENT_CONFIGURATOR );

        addEntity( COMPONENT_CONFIGURATOR, componentConfigurator );
    }
}
