package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentDependency
{
    private static final String DEAULT_DEPENDENCY_TYPE = "jar";
    
    /** */
    private String groupId;

    /** */
    private String artifactId;

    /** */
    private String type = DEAULT_DEPENDENCY_TYPE;

    /** */
    private String version;
    
    /**
     * @return Returns the artifactId.
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param artifactId The artifactId to set.
     */
    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    /**
     * @return Returns the groupId.
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "groupId:artifactId:version:type = " + groupId + ":" + artifactId + ":" + version + ":" + type );

        return sb.toString();
    }
}
