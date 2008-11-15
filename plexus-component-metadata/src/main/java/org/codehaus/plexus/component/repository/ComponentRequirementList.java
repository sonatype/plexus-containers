package org.codehaus.plexus.component.repository;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Andrew Williams
 * @version $Id: ComponentRequirementList.java 6965 2007-10-21 05:32:27Z jvanzyl $
 * @since 1.0
 */
public class ComponentRequirementList
    extends ComponentRequirement 
{
    private List roleHints;

    public List getRoleHints()
    {
        return roleHints;
    }

    public void setRoleHints(List roleHints)
    {
        this.roleHints = roleHints;
    }

    public String getRoleHint()
    {
        StringBuffer ret = new StringBuffer();
        Iterator iter = getRoleHints().iterator();

        while (iter.hasNext()) {
            String hint = (String) iter.next();
            ret.append(hint);

            if (iter.hasNext())
            {
                ret.append(",");
            }
        }

        return ret.toString();
    }
}
