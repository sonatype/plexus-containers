package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Initializable
{
    public void initialize()
        throws InitializationException;
}
