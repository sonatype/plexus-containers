package org.codehaus.plexus.configuration.xstream.pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Pipeline
{
    private List valves = new ArrayList();

    public Pipeline()
    {
    }

    public Valve getValve( int i )
    {
        return (Valve) valves.get( i );
    }
}
