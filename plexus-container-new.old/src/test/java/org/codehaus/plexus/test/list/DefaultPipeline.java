package org.codehaus.plexus.test.list;

import java.util.List;
import java.util.Iterator;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultPipeline
    implements Pipeline
{
    private List valves;

    public void execute()
    {
        for ( Iterator i = valves.iterator(); i.hasNext(); )
        {
            ((Valve) i.next()).execute();
        }
    }

    public List getValves()
    {
        return valves;
    }
}
