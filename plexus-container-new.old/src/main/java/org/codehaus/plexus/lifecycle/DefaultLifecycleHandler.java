package org.codehaus.plexus.lifecycle;



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
public class DefaultLifecycleHandler
    extends AbstractLifecycleHandler
{
    public DefaultLifecycleHandler()
    {
        super();
    }

    public void initialize()
        throws Exception
    {
    }
}
