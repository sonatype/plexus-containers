package org.codehaus.plexus.test.discovery;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class MavenPluginDescriptor
    extends ComponentDescriptor
{
    private String id;

    private List goals;

    public String getRole()
    {
        return "org.codehaus.plexus.test.discovery.MavenPlugin";
    }

    public String getRoleHint()
    {
        return id;
    }

    public List getGoals()
    {
        return goals;
    }
}
