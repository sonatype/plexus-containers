package org.codehaus.plexus.component.repository;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentProfileDescriptor
{
    /** Component Factory Id. */
    private String componentFactoryId;

    /** Lifecycle Handler Id. */
    private String lifecycleHandlerId;

    /** Component Manager Id. */
    private String componentManagerId;
        
    /** Component Composer Id. */
    private String componentComposerId;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public String getComponentFactoryId()
    {
        return componentFactoryId;
    }

    public void setComponentFactoryId( String componentFactoryId )
    {
        this.componentFactoryId = componentFactoryId;
    }

    public String getLifecycleHandlerId()
    {
        return lifecycleHandlerId;
    }

    public void setLifecycleHandlerId( String lifecycleHandlerId )
    {
        this.lifecycleHandlerId = lifecycleHandlerId;
    }

    public String getComponentManagerId()
    {
        return componentManagerId;
    }

    public void setComponentManagerId( String componentManagerId )
    {
        this.componentManagerId = componentManagerId;
    }
    
    
    public String getComponentComposerId()
    {
        return componentComposerId;
    }
    
    public void setComponentComposerId( String componentComposerId )
    {
        this.componentComposerId = componentComposerId;
    }
}
