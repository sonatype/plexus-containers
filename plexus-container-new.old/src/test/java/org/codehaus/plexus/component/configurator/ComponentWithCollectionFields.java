package org.codehaus.plexus.component.configurator;

import java.util.Vector;
import java.util.List;
import java.util.HashSet;

/**
 *
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 *
 * @version $Id$
 */
public class ComponentWithCollectionFields
{
    Vector vector;

    HashSet set;

    List list;

    public Vector getVector()
    {
        return vector;
    }

    public HashSet getSet()
    {
        return set;
    }

    public List getList()
    {
        return list;
    }
}
