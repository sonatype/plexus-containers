package org.codehaus.plexus.component.repository;

import org.apache.avalon.framework.configuration.Configuration;

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
    private String role;

    /** Role hint. */
    private String roleHint;

    /** Name of the component class. */
    private String implementation;

    /** Configuration for the component. */
    private Configuration configuration;

    /** Instantiation strategy. */
    private String instantiationStrategy;

    /** Which lifecyclehandler to use. If null, use the containers default one. */
    private String lifecycleHandler;

    /** Component profile id. */
    private String componentProfile;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /** Construct.
     */
    public ComponentDescriptor()
    {
    }

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

    /** Set the role name of the component.
     *
     *  @param role The role name.
     */
    public void setRole( String role )
    {
        this.role = role;
    }

    /** Retrieve the role name of the component.
     *
     *  @return THe role name.
     */
    public String getRole()
    {
        return role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    /** Set the class name of the component.
     *
     *  @param implementation The class name of the component.
     */
    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    /** Retrieve the class name of the component.
     *
     *  @return The class name.
     */
    public String getImplementation()
    {
        return implementation;
    }

    /**
     *
     * @return
     */
    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    /**
     *
     * @param instantiationStrategy
     */
    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }

    /** Set the <code>Configuration</code> for the component.
     *
     *  @param configuration The configuration.
     */
    public void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    /** Retrieve the <code>Configuration</code> for the component.
     *
     *  @return The configuration.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return
     */
    public String getLifecycleHandler()
    {
        return lifecycleHandler;
    }

    /**
     * Set the id of the lifecycle handler the component uses
     *
     * @param id
     */
    public void setLifecycleHandler( String id )
    {
        lifecycleHandler = id;
    }

    public String getComponentProfile()
    {
        return componentProfile;
    }

    public void setComponentProfile( String componentProfile )
    {
        this.componentProfile = componentProfile;
    }
}
