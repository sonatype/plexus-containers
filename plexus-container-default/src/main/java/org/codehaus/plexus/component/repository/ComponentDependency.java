package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentDependency
{
    private static final String DEAULT_DEPENDENCY_TYPE = "jar";
    
    private String groupId;

    private String artifactId;

    private String type = DEAULT_DEPENDENCY_TYPE;

    private String version;
    
    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "groupId = " ).append( groupId ).
            append( ", artifactId = " ).append( artifactId ).
            append( ", version = " ).append( version ).
            append( ", type = " ).append( type );

        return sb.toString();
    }
}
