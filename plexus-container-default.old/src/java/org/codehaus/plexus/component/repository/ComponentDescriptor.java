package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Component instantiation description.
 *
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ComponentDescriptor
{
    private String alias = null;

    private String role = null;

    private String roleHint = null;

    private String implementation = null;

    private String version = null;

    private String componentType = null;

    private PlexusConfiguration configuration = null;

    private String instantiationStrategy = null;

    private String lifecycleHandler = null;

    private String componentProfile = null;

    private Set requirements;

    private String componentFactory;

    private String componentComposer;

    private String description;

    // ----------------------------------------------------------------------
    // These two fields allow for the specification of an isolated class realm
    // and dependencies that might be specified in a component configuration
    // setup by a user i.e. this is here to allow isolation for components
    // that are not picked up by the discovery mechanism.
    // ----------------------------------------------------------------------

    private boolean isolatedRealm;

    private List dependencies;

    // ----------------------------------------------------------------------

    private ComponentSetDescriptor componentSetDescriptor;

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

    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append(" role: '" + role + "'" );            

        key.append( ", implementation: '" + implementation  + "'" );

        if ( roleHint != null )
        {
            key.append( ", roleHint: '" + roleHint  + "'" );
        }

        if ( alias != null )
        {
            key.append( ", alias: '" + alias  + "'" );
        }

        String retValue = key.toString();

        return retValue;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias( String alias )
    {
        this.alias = alias;
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

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
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

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getComponentType()
    {
        return componentType;
    }

    public void setComponentType( String componentType )
    {
        this.componentType = componentType;
    }

    public String getInstantiationStrategy()
    {
        return instantiationStrategy;
    }

    public PlexusConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( PlexusConfiguration configuration )
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

    public void addRequirement( final ComponentRequirement requirement )
    {
        getRequirements().add( requirement );
    }

    public Set getRequirements()
    {
        if ( requirements == null )
        {
            requirements = new HashSet();
        }
        return requirements;
    }

    public String getComponentFactory()
    {
        return componentFactory;
    }

    public void setComponentFactory( String componentFactory )
    {
        this.componentFactory = componentFactory;
    }

    public String getComponentComposer()
    {
        return componentComposer;
    }

    public void setComponentComposer( String componentComposer )
    {
        this.componentComposer = componentComposer;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setInstantiationStrategy( String instantiationStrategy )
    {
        this.instantiationStrategy = instantiationStrategy;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public boolean isIsolatedRealm()
    {
        return isolatedRealm;
    }

    public void setComponentSetDescriptor( ComponentSetDescriptor componentSetDescriptor )
    {
        this.componentSetDescriptor = componentSetDescriptor;
    }

    public void setIsolatedRealm( boolean isolatedRealm )
    {
        this.isolatedRealm = isolatedRealm;
    }

    public List getDependencies()
    {
        return dependencies;
    }

}
