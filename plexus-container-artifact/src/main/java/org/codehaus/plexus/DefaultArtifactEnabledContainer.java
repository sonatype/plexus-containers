package org.codehaus.plexus;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author jdcasey
 * @version $Id$
 */
public class DefaultArtifactEnabledContainer
    extends DefaultPlexusContainer
    implements ArtifactEnabledContainer
{
    public DefaultArtifactEnabledContainer()
    {
        super();
    }

    private Artifact createArtifact( ComponentDependency cd )
    {
        return new DefaultArtifact( cd.getGroupId(), cd.getArtifactId(), cd.getVersion(), "jar" );
    }

    // ----------------------------------------------------------------------
    // Dynamic component addition
    // ----------------------------------------------------------------------

    // We could have an option here for isolation ...

    int realmTmpId = 0;

    public void addComponent( Artifact component,
                              ArtifactResolver artifactResolver,
                              Set remoteRepositories,
                              ArtifactRepository localRepository,
                              ArtifactMetadataSource sourceReader,
                              ArtifactFilter filter )
        throws Exception
    {
        boolean dependencyComponentsDiscovered = false;

        // I need an active filter and not an excludes list here.

        // We have to create a completely fake realm here so that only the
        // components
        // in the JAR we don't want the process to touch any parent realms or
        // ClassLoaders.

        // ----------------------------------------------------------------------
        // First we need to see if the artifact is present
        // ----------------------------------------------------------------------

        artifactResolver.resolve( component, remoteRepositories, localRepository );

        realmTmpId++;

        ClassWorld classWorld = getClassWorld();

        ClassRealm tmp = classWorld.newRealm( "tmp" + realmTmpId );

        tmp.addConstituent( component.getFile().toURL() );

        List componentDescriptors = discoverComponents( tmp );

        classWorld.disposeRealm( "tmp" + realmTmpId );

        // ----------------------------------------------------------------------
        // Now we walk through any of the component descriptors that we find in
        // the JAR of the component we are adding and there may be several of
        // them.
        //
        // NOTE: We must still deal with components that might be found in the
        // depenendencies of this component. For example a Maven plugin has
        // component descriptors in the plugin JAR itself, but if it relies on
        // components that are found within the dependent JARs then we must
        // account for that as well.
        //
        // To make this easier we should probably get something back from the
        // discovery process which has the individual component descriptors as
        // well as a list of their dependencies. Right now this is being hacked
        // in by repeating the list of dependencies for each component and
        // attaching them. The indivdual list of dependencies may indeed by
        // useful but I really only need to process them here once insofar as
        // discovering components inside the dependencies. jvz.
        // ----------------------------------------------------------------------

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            ComponentDescriptor componentDescriptor = (ComponentDescriptor) i.next();

            String componentKey = componentDescriptor.getComponentKey();

            ClassRealm componentRealm;

            ClassRealm plexusRealm = getContainerRealm();

            componentRealm = plexusRealm.createChildRealm( componentKey );

            if ( componentDescriptor.getComponentSetDescriptor().getDependencies() != null )
            {
                Set artifactsToResolve = new HashSet();

                for ( Iterator j = componentDescriptor.getComponentSetDescriptor().getDependencies().iterator(); j.hasNext(); )
                {
                    ComponentDependency cd = (ComponentDependency) j.next();

                    // ----------------------------------------------------------------------
                    // Don't even attempt to transtively resolve artifacts we are excluding.
                    // ----------------------------------------------------------------------

                    if ( filter.include( cd.getArtifactId() ) )
                    {
                        artifactsToResolve.add( createArtifact( cd ) );
                    }
                }

                // ----------------------------------------------------------------------
                // I need to pass the exclusion parameters into the artifact resolution
                // phase to prevent duplication entries.
                // ----------------------------------------------------------------------

                ArtifactResolutionResult result =
                    artifactResolver.resolveTransitively( artifactsToResolve,
                                                          remoteRepositories,
                                                          localRepository,
                                                          sourceReader,
                                                          filter );

                for ( Iterator k = result.getArtifacts().values().iterator(); k.hasNext(); )
                {
                    Artifact a = (Artifact) k.next();

                    if ( filter.include( a.getArtifactId() ) )
                    {
                        componentRealm.addConstituent( a.getFile().toURL() );
                    }
                }
            }

            // ----------------------------------------------------------------------
            // Now all the dependencies have been processed and we have a realm that
            // is full of the components dependencies. We have already discovered
            // components in the JAR that was passed into this method so at this
            // point all we need to do is discover any components that may be
            // contained within the dependencies.
            // ----------------------------------------------------------------------

            if ( !dependencyComponentsDiscovered )
            {
                List dependencyComponents = discoverComponents( componentRealm );

                // ----------------------------------------------------------------------
                // We have to make sure that components among the dependencies have
                // their realms aligned with the realm of the component that was added.
                // If this is not done then component will use the container realm which
                // will not contain the classes required for the component because they
                // are put in the added component's realm here.
                // ----------------------------------------------------------------------

                for ( Iterator j = dependencyComponents.iterator(); j.hasNext(); )
                {
                    ComponentDescriptor dcd = (ComponentDescriptor) j.next();

                    addRealmAlias( dcd.getComponentKey(), componentKey );
                }

                dependencyComponentsDiscovered = true;
            }

            // ----------------------------------------------------------------------
            // Now that the dependencies have been processed we can add the component
            // JAR itself into the realm and everything is now ready for use.
            // ----------------------------------------------------------------------

            componentRealm.addConstituent( component.getFile().toURL() );
        }
    }

    protected boolean includeArtifact( String artifactId, String[] artifactExcludes )
    {
        for ( int b = 0; b < artifactExcludes.length; b++ )
        {
            if ( artifactId.equals( artifactExcludes[b] ) )
            {
                return false;
            }
        }

        return true;
    }
}
