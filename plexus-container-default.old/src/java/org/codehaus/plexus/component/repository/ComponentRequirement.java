package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a> 
 * @version $Id$ 
 * @todo Maybe hashCode and equals should use only 'role' 
 */
public final class ComponentRequirement
{
    String role;
    String fieldName;
    
    /**
     * @return Returns the fieldName.
     */
    public String getFieldName()
    {
        return fieldName;
    }
    /**
     * @param fieldName The fieldName to set.
     */
    public void setFieldName( final String fieldName )
    {
        this.fieldName = fieldName;
    }
    /**
     * @return Returns the role.
     */
    public String getRole()
    {
        return role;
    }
    /**
     * @param role The role to set.
     */
    public void setRole( final String role )
    {
        this.role = role;
    }

    public String toString()
    {
        return "org.codehaus.plexus.component.repository.ComponentRequirement{" +
                "role='" + role + "'" +
                ", fieldName='" + fieldName + "'" +
                "}";
    }


}
