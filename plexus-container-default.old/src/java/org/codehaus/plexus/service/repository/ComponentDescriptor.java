package org.codehaus.plexus.service.repository;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import org.apache.avalon.framework.configuration.Configuration;

import java.util.Map;

/** Component instantiation description.
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 */
public class ComponentDescriptor
{
    // ----------------------------------------------------------------------
    //  Instance members
    // ----------------------------------------------------------------------

    /** Component role name. */
    private String role;

    /** Role hint. */
    private String roleHint;

    /** Component id, unique within a role, for identified component instance. */
    private String id;

    /** Name of the component class. */
    private String implementation;

    /** Configuration for the component. */
    private Configuration configuration;

    /** Parameters for the component. */
    private Map parameters;

    /** Instantiation strategy. */
    private String instantiationStrategy;

    /** Which lifecyclehandler to use. If null, use the containers default one. */
    private String lifecycleHandlerId;

    // ----------------------------------------------------------------------
    //  Constructors
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
        if ( getId() != null )
        {
            return getRole() + getId();
        }
        else if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }

    /** Set the id.
     *
     *  <p>
     *  The id must be unique within a role if this component
     *  is an identified component.  If it is a factory-produced
     *  or other unindentified component, an id of '*' is already
     *  the default.
     *  </p>
     *
     *  @param id The component's id.
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /** Retrieve the id.
     *
     *  <p>
     *  This id will be unique within a role if this component
     *  is an identified component.  If it is a factory-produced
     *  or other unindentified component, the id is '*".
     *  </p>
     *
     *  @return The id.
     */
    public String getId()
    {
        return this.id;
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

    /** Set the <code>Configuration</code> for the component.
     *
     *  @param parameters The configuration.
     */
    public void setParameters( Map parameters )
    {
        this.parameters = parameters;
    }

    /** Retrieve the <code>Configuration</code> for the component.
     *
     *  @return The configuration.
     */
    public Map getParameters()
    {
        return parameters;
    }
    /**
     * @return
     */
    public String getLifecycleHandlerId()
    {
        return lifecycleHandlerId;
    }

    /**
     * Set the id of the lifecycle handler the component uses
     *
     * @param string
     */
    public void setLifecycleHandlerId(String id)
    {
        lifecycleHandlerId = id;
    }

}
