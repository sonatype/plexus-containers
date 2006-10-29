package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Contextualizable
{
    public void contextualize( Context context )
        throws ContextException;
}
