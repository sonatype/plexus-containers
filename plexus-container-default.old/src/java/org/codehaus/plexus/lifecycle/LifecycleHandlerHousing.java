package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.configuration.Configuration;

/**
 * Holds a LifecycleHandler.
 *
 * <p>Created on 19/07/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class LifecycleHandlerHousing
{
    /**
     * LifecycleHandler manager.
     */
    private LifecycleHandler handler;

    /**
     * Configuration for the lifecycle handler
     */
    private Configuration configuration;

    /**
     * Lifecycle Handler implementation
     */
    private String implementation;

    /** Unique id for the held LifecycleHandler type. This id is used by
     * components to specify their lifecycle handler.*/
    private String id;

    /** Used by humans?*/
    private String name;

    /**
     * @return
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return
     */
    public LifecycleHandler getHandler()
    {
        return handler;
    }

    /**
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return
     */
    public String getImplementation()
    {
        return implementation;
    }

    /**
     * @param configuration
     */
    public void setConfiguration( Configuration configuration )
    {
        this.configuration = configuration;
    }

    /**
     * @param handler
     */
    public void setHandler( LifecycleHandler handler )
    {
        this.handler = handler;
    }

    /**
     * @param id
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @param implementation
     */
    public void setImplementation( String implementation )
    {
        this.implementation = implementation;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     */
    public void setName( String name )
    {
        this.name = name;
    }

}
