package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentComposer
    extends AbstractLogEnabled
    implements ComponentComposer
{
    private String id;

    public String getId()
    {
        return id;
    }

}
