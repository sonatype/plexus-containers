package org.codehaus.plexus.component.composition;

import java.util.List;

/**
 * @author Jason van Zyl
 * @version $Revision: Requirement.java 3041 2006-02-14 17:14:36Z jvanzyl $
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
