package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;

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
    /** Component role name. */
    private String role = null;

    /** Role hint. */
    private String roleHint = null;

    /** Name of the component class. */
    private String implementation = null;

    /** Configuration for the component. */
    private Configuration configuration = null;

    /** Instantiation strategy. */
    private String instantiationStrategy = null;

    /** Which lifecyclehandler to use. If null, use the containers default one. */
    private String lifecycleHandler = null;

    /** Component profile id. */
    private String componentProfile = null;

    /** List of required component interfaces. */
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

    /** Retrieve the role name of the component.
     *
     *  @return THe role name.
     */
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

    /** Retrieve the class name of the component.
     *
     *  @return The class name.
     */
    public String getImplementation()
    {
        return implementation;
    }

    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    /**
     *
     * @return
     */
    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    /** Retrieve the <code>Configuration</code> for the component.
     *
     *  @return The configuration.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    /**
     * @return
     */
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
