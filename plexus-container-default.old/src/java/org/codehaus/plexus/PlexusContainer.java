package org.codehaus.plexus;

import org.codehaus.plexus.configuration.ConfigurationResourceException;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.classworlds.ClassWorld;

import java.io.Reader;

public interface PlexusContainer
{
    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /** */
    public void initialize()
        throws Exception;

    /** */
    public void start()
        throws Exception;

    /** */
    public void dispose()
        throws Exception;

    // ----------------------------------------------------------------------
    // Pre-initialization - can only be called prior to initialization
    // ----------------------------------------------------------------------

    /** */
    public void addContextValue( Object key, Object value );

    /** */
    public void setClassWorld( ClassWorld classWorld );

    /** */
    public void setClassLoader( ClassLoader classLoader );

    /** */
    public void setConfigurationResource( Reader configuration )
        throws ConfigurationResourceException;

    // ----------------------------------------------------------------------
    // Post-initialization - can only be called post initialization
    // ----------------------------------------------------------------------

    /** *//*
    public LifecycleHandler getLifecycleHandler();


	public LifecycleHandler getLifecycleHandler(String id);*/
    /** */
    public ClassLoader getClassLoader();

    /** */
    public ComponentRepository getComponentRepository();
}
