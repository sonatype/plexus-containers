package org.codehaus.plexus.service.repository;

import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.service.repository.instance.InstanceManager;

import com.werken.classworlds.ConfigurationException;

/** 
 * House the information for a component
 * and the instance manager which performs the 
 * management of this component on behalf of this 
 * ComponentManager.
 * 
 * <p>This is used so that the instance managers can be pluggable</p>
 *
 */
public class ComponentManager
{
    /** Component descriptor. */
    private ComponentDescriptor componentDescriptor;
    /** Instance Manager descriptor. */
    private ComponentDescriptor instanceManagerDescriptor;
    /**  Instance Manager. */
    private InstanceManager instanceManager;
    /** ClassLoader */
    private ClassLoader classLoader;
    /** Component Repository. */
    private ComponentRepository componentRespository;

    /** Constuctor. */
    public ComponentManager(
        ComponentDescriptor componentDescriptor,
        ComponentRepository componentRepository,
        ComponentDescriptor instanceManagerDescriptor,
        ClassLoader classLoader)
    {
        this.componentDescriptor = componentDescriptor;
        this.componentRespository = componentRepository;
        this.instanceManagerDescriptor = instanceManagerDescriptor;
        this.classLoader = classLoader;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize() throws Exception
    {
        Class c = classLoader.loadClass(instanceManagerDescriptor.getImplementation());
        instanceManager = (InstanceManager) c.newInstance();
        instanceManager.setClassLoader(classLoader);
        instanceManager.setComponentImplementation(componentDescriptor.getImplementation());
		instanceManager.setComponentManager( this );
        //the lifecyclehandler used is based on the component descriptor        
        //look it up from the component repository
        String id = componentDescriptor.getLifecycleHandlerId();
        if (id == null)
        {
            //use the default handler
            instanceManager.setLifecycleHandler(
                getComponentRespository().getDefaultLifecycleHandler());
        }
        else
        {
            LifecycleHandler lh;
            try
            {
                lh = getComponentRespository().getLifecycleHandler(id);
            }
            catch (UndefinedLifecycleHandlerException e)
            {
                throw new ConfigurationException(
                    "No LifecycleHandler confgured with id:"
                        + id
                        + " required by component with role:"
                        + componentDescriptor.getRole());
            }
            instanceManager.setLifecycleHandler(lh);
        }
        instanceManager.initialize();
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    /**
     *
     * @return
     */
    public ComponentDescriptor getComponentDescriptor()
    {
        return componentDescriptor;
    }

    /**
     *
     * @param serviceDescriptor
     */
    public void setComponentDescriptor( ComponentDescriptor serviceDescriptor )
    {
        this.componentDescriptor = serviceDescriptor;
    }

    /**
     *
     * @return
     */
    public ComponentRepository getComponentRespository()
    {
        return componentRespository;
    }

	/**
	 *
	 * @param componentRespository
	 */
	public void setComponentRespository( ComponentRepository componentRespository )
	{
		this.componentRespository = componentRespository;
	}

    /**
     * Release the component back to this manager. 
     * 
     * @param component
     */
    public void release(Object component)
    {
        if (component != null)
        {
            getInstanceManager().release(component);
        }
    }

    /**
     * Obtain the component this manager manages.
     * 
     * @return
     */
    public Object getComponent() throws Exception
    {
        return getInstanceManager().getComponent();
    }
    /**
     *
     * @return
     *//*
    public ComponentHousing getComponentHousing() throws ServiceException
    {
        try
        {
            return getInstanceManager().getInstance();
        }
        catch (Exception e)
        {
            throw new ServiceException("instance-manager", e.getMessage(), e);
        }
    }*/

    private InstanceManager getInstanceManager()
    {
        return instanceManager;
    }

    /*   public void setInstanceManager( InstanceManager instanceManager )
       {
           this.instanceManager = instanceManager;
       }*/

    /**
     * Dispose this manager. This will also cause all components this manager 
     * manages to be disposed.
     */
    public void dispose()
    {
        getInstanceManager().dispose();
    }
}
