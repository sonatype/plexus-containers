package org.codehaus.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.ComponentSelector;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.UndefinedComponentComposerException;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.session.SessionException;
import org.codehaus.plexus.session.SessionId;
import org.codehaus.plexus.session.SessionTimeoutException;

import java.io.File;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

    // ----------------------------------------------------------------------
    // Timestamp access
    // ----------------------------------------------------------------------

    public Date getCreationDate();

    // ----------------------------------------------------------------------
    // Child container access
    // ----------------------------------------------------------------------

    boolean hasChildContainer( String name );

    void removeChildContainer( String name );

    PlexusContainer getChildContainer( String name );

    PlexusContainer createChildContainer( String name, List classpathJars, Map context )
        throws PlexusContainerException;

    PlexusContainer createChildContainer( String name, List classpathJars, Map context, List discoveryListeners )
        throws PlexusContainerException;

    // ----------------------------------------------------------------------
    // Session management
    // ----------------------------------------------------------------------
    
    SessionId createSession()
        throws PlexusContainerException;
    
    void closeSession( SessionId sessionId );
    
    void touchSession( SessionId sessionId ) throws SessionException;

    // ----------------------------------------------------------------------
    // Component lookup
    // ----------------------------------------------------------------------

    Object lookup( String componentKey )
        throws ComponentLookupException;

    Object lookup( String role, String roleHint )
        throws ComponentLookupException;

    Map lookupMap( String role )
        throws ComponentLookupException;

    List lookupList( String role )
        throws ComponentLookupException;
    
    // ----------------------------------------------------------------------
    // Session-based Lookup and Management
    // ----------------------------------------------------------------------

    void registerSelector( ComponentSelector selector, SessionId sessionId )
        throws PlexusContainerException, SessionException;

    void deregisterSelector( ComponentSelector selector, SessionId sessionId )
        throws PlexusContainerException, SessionException;

    Object lookup( String componentKey, SessionId sessionId )
        throws ComponentLookupException, SessionException;

    Map lookupMap( String role, SessionId sessionId )
        throws ComponentLookupException, SessionException;

    List lookupList( String role, SessionId sessionId )
        throws ComponentLookupException, SessionException;

    // ----------------------------------------------------------------------
    // Component Descriptor Lookup
    // ----------------------------------------------------------------------

    ComponentDescriptor getComponentDescriptor( String componentKey );

    Map getComponentDescriptorMap( String role );

    List getComponentDescriptorList( String role );

    void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws ComponentRepositoryException;

    // ----------------------------------------------------------------------
    // Component release
    // ----------------------------------------------------------------------

    void release( Object component )
        throws ComponentLifecycleException;

    void releaseAll( Map components )
        throws ComponentLifecycleException;

    void releaseAll( List components )
        throws ComponentLifecycleException;

    // ----------------------------------------------------------------------
    // Component discovery
    // ----------------------------------------------------------------------

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String roleHint );

    // ----------------------------------------------------------------------
    // Component replacement
    // ----------------------------------------------------------------------

    void suspend( Object component )
        throws ComponentLifecycleException;

    void resume( Object component )
        throws ComponentLifecycleException;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    void initialize()
        throws PlexusContainerException;

    boolean isInitialized();

    void start()
        throws PlexusContainerException;

    boolean isStarted();

    void dispose();

    // ----------------------------------------------------------------------
    // Context
    // ----------------------------------------------------------------------

    Context getContext();

    // ----------------------------------------------------------------------
    // Container setup
    // ----------------------------------------------------------------------

    void setParentPlexusContainer( PlexusContainer parentContainer );

    void addContextValue( Object key, Object value );

    void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException;

    Logger getLogger();

    Object createComponentInstance( ComponentDescriptor componentDescriptor )
        throws ComponentInstantiationException, ComponentLifecycleException;

    void composeComponent( Object component, ComponentDescriptor componentDescriptor )
        throws CompositionException, UndefinedComponentComposerException;

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void addJarRepository( File repository );

    void addJarResource( File resource )
        throws PlexusContainerException;

    ClassRealm getContainerRealm();

    /** @deprecated Use getContainerRealm() instead. */
    ClassRealm getComponentRealm( String componentKey );

    // ----------------------------------------------------------------------
    // Start of new programmatic API to fully control the container
    // ----------------------------------------------------------------------

    void setLoggerManager( LoggerManager loggerManager );

    LoggerManager getLoggerManager();

    // ----------------------------------------------------------------------
    // Autowiring Support
    // ----------------------------------------------------------------------

    Object autowire( Object component )
        throws CompositionException;

    Object createAndAutowire( String clazz )
        throws CompositionException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    // ----------------------------------------------------------------------
    // Reloading
    // ----------------------------------------------------------------------

    void setReloadingEnabled( boolean reloadingEnabled );

    boolean isReloadingEnabled();
}
