package org.codehaus.plexus.component.discovery;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DiscoveryListenerDescriptor
{
    private String role;

    private String roleHint;

    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }
}
