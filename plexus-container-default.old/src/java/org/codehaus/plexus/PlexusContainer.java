package org.codehaus.plexus;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PlexusContainer
{
    String ROLE = PlexusContainer.class.getName();

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
        throws Exception;

    void releaseAll( Map components )
        throws Exception;

    void releaseAll( List components )
        throws Exception;

    // ----------------------------------------------------------------------
    // Component discovery
    // ----------------------------------------------------------------------

    boolean hasComponent( String componentKey );

    boolean hasComponent( String role, String roleHint );

    // ----------------------------------------------------------------------
    // Component replacement
    // ----------------------------------------------------------------------

    void suspend( Object component )
        throws Exception;

    void resume( Object component )
        throws Exception;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    void initialize()
        throws Exception;

    void start()
        throws Exception;

    void dispose()
        throws Exception;

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
        throws Exception;

    void composeComponent( Object component, ComponentDescriptor componentDescriptor )
        throws Exception;

    // ----------------------------------------------------------------------
    // Discovery
    // ----------------------------------------------------------------------

    void registerComponentDiscoveryListener( ComponentDiscoveryListener listener );

    void removeComponentDiscoveryListener( ComponentDiscoveryListener listener );

    //void discoverComponents()
      //  throws Exception;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void addJarRepository( File repository )
        throws Exception;

    public void addComponent( Artifact component,
                              ArtifactResolver artifactResolver,
                              Set remoteRepositories,
                              ArtifactRepository localRepository,
                              ArtifactMetadataSource sourceReader,
                              String[] groupExcludes )
        throws Exception;
}
