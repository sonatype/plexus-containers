package org.codehaus.plexus;

import com.werken.classworlds.ClassWorld;

import org.codehaus.plexus.configuration.ConfigurationResourceException;
import org.codehaus.plexus.service.repository.ComponentRepository;

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
    public void dispose();

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
