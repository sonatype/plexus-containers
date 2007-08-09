package org.codehaus.plexus.component.repository;

import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0
 */
public final class ComponentRequirementList
        extends ComponentRequirement {
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
