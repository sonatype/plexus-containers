package org.codehaus.plexus.configuration.xstream;


/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Message
{
    /** Group id. */
    private String groupId;

    /** Group name. */
    private String groupName;

    /** Group version. */
    private String groupVersion;

    /** Id. */
    private String id;

    /** Name. */
    private String name;

    /** Class name. */
    private String className;

    /** Expression used to extract unique id from this type of message. */
    private String uniqueIdExpression;

    /** Expression used to extract the recipient TPI id from this type of message. */
    private String recipientIdExpression;

    /** Intake id. */
    private String intakeId;

    /** Outtake id. */
    private String outtakeId;

    /** String view id. */
    private String viewId;

    /** Summary. */
    private Summary summary;

    /** Inner class. */
    private InnerClass innerClass;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName( String groupName )
    {
        this.groupName = groupName;
    }

    public String getGroupVersion()
    {
        return groupVersion;
    }

    public void setGroupVersion( String groupVersion )
    {
        this.groupVersion = groupVersion;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName( String className )
    {
        this.className = className;
    }

    public String getUniqueIdExpression()
    {
        return uniqueIdExpression;
    }

    public void setUniqueIdExpression( String uniqueIdExpression )
    {
        this.uniqueIdExpression = uniqueIdExpression;
    }

    public String getRecipientIdExpression()
    {
        return recipientIdExpression;
    }

    public void setRecipientIdExpression( String recipientIdExpression )
    {
        this.recipientIdExpression = recipientIdExpression;
    }

    public String getIntakeId()
    {
        return intakeId;
    }

    public void setIntakeId( String intakeId )
    {
        this.intakeId = intakeId;
    }

    public String getOuttakeId()
    {
        return outtakeId;
    }

    public void setOuttakeId( String outtakeId )
    {
        this.outtakeId = outtakeId;
    }

    public String getViewId()
    {
        return viewId;
    }

    public void setViewId( String viewId )
    {
        this.viewId = viewId;
    }

    public Summary getSummary()
    {
        return summary;
    }

    public void setSummary( Summary summary )
    {
        this.summary = summary;
    }

    public InnerClass getInnerClass()
    {
        return innerClass;
    }

    public void setInnerClass( InnerClass innerClass )
    {
        this.innerClass = innerClass;
    }

    static class InnerClass
    {
        private String id;

        public String getId()
        {
            return id;
        }

        public void setId( String id )
        {
            this.id = id;
        }
    }
}
