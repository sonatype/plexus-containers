package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a> 
 * 
 * @version $Id$ 
 * @todo Maybe hashCode and equals should use only 'role' 
 */
public final class ComponentRequirement
{
    private String role;

    private String roleHint;

    private String fieldName;
    
    private String fieldMappingType;
    
    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( final String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( final String roleHint )
    {
        this.roleHint = roleHint;
    }

    public String getRequirementKey()
    {
        if ( getRoleHint() != null )
        {
            return getRole() + getRoleHint();
        }

        return getRole();
    }



    public String toString()
    {
        return "ComponentRequirement{" +
               "role='" + role + "'" +
               ", roleHint='" + roleHint + "'" +
               ", fieldName='" + fieldName + "'" +
               "}";
    }

    /**
     * 
     */
    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append( "role: '");
        
        key.append( getRole() );

        key.append( "'" );

        if ( getRoleHint() != null )
        {
            key.append( ", role-hint: '" );

            key.append( getRoleHint() );

            key.append( "'. " );
        }

        if ( getFieldName() != null )
        {
            key.append( ", field name: '" );

            key.append( getFieldName() );

            key.append( "' " );
        }
        
        String retValue = key.toString();
        
        return retValue;
       
    }

    public String getFieldMappingType()
    {
        return fieldMappingType;
    }

    public void setFieldMappingType( String fieldType )
    {
        this.fieldMappingType = fieldType;
    }
    
    public boolean equals( Object other )
    {
        if ( other instanceof ComponentRequirement )
        {
            String myId = role + ":" + roleHint;
            
            ComponentRequirement req = (ComponentRequirement) other;
            String otherId = req.role + ":" + req.roleHint;
            
            return myId.equals( otherId );
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return ( role + ":" + roleHint ).hashCode();
    }


}
