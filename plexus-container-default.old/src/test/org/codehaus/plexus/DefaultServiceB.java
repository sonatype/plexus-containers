package org.codehaus.plexus;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * A simple native plexus component.
 */
public class DefaultServiceB
    extends AbstractLogEnabled
    implements ServiceB, Contextualizable, Initializable, Startable
{
    public boolean enableLogging;
    public boolean contextualize;
    public boolean initialize;
    public boolean start;
    public boolean stop;

    public void enableLogging( Logger logger )
    {
        enableLogging = true;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        contextualize = true;
    }

    public void initialize()
        throws Exception
    {
        initialize = true;
    }

    public void start()
        throws Exception
    {
        start = true;
    }

    public void stop()
        throws Exception
    {
        stop = true;
    }
}
