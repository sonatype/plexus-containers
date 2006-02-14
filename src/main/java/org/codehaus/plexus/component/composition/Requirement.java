package org.codehaus.plexus.component.composition;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class Requirement
{
    private Object assignment;

    private List componentDescriptors;

    public Requirement( Object assignment, List componentDescriptors )
    {
        this.assignment = assignment;

        this.componentDescriptors = componentDescriptors;
    }

    public Object getAssignment()
    {
        return assignment;
    }

    public List getComponentDescriptors()
    {
        return componentDescriptors;
    }
}
