package org.codehaus.plexus.lifecycle;



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
