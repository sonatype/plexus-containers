package org.codehaus.plexus.component.repository;


import org.codehaus.plexus.configuration.Configuration;

import java.util.HashSet;
import java.util.Set;

/** Component instantiation description.
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 */
public class ComponentDescriptor
{
    private String role = null;

    private String roleHint = null;

    private String implementation = null;

    private String version = null;

    private Configuration configuration = null;

    private String instantiationStrategy = null;

    private String lifecycleHandler = null;

    private String componentProfile = null;

    private Set requirements;

    // ----------------------------------------------------------------------
    //  Instance methods
    // ----------------------------------------------------------------------

    public String getComponentKey()
    {
        if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public String getImplementation()
    {
        return implementation;
    }

    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    public String getVersion()
    {
        return version;
    }

    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    public boolean hasConfiguration()
    {
        return configuration != null;
    }

    public String getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    public String getComponentProfile()
    {
        return componentProfile;
    }

    public Set getRequirements()
    {
        if ( requirements == null )
        {
            requirements = new HashSet();
        }

        return requirements;
    }
}
