package org.codehaus.plexus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

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

    public void addComponent( Artifact component, ArtifactResolver artifactResolver, Set remoteRepositories,
        ArtifactRepository localRepository, ArtifactMetadataSource sourceReader, String[] artifactExcludes )
        throws Exception
    {
        boolean isolatedRealm = false;

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

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            ComponentDescriptor componentDescriptor = (ComponentDescriptor) i.next();

            String componentKey = componentDescriptor.getComponentKey();

            ClassRealm componentRealm;

            if ( isolatedRealm )
            {
                componentRealm = classWorld.newRealm( componentKey );
            }
            else
            {
                ClassRealm plexusRealm = getContainerRealm();
                componentRealm = plexusRealm.createChildRealm( componentKey );
            }

            componentRealm.addConstituent( component.getFile().toURL() );

            if ( componentDescriptor.getComponentSetDescriptor().getDependencies() != null )
            {
                Set artifactsToResolve = new HashSet();

                for ( Iterator j = componentDescriptor.getComponentSetDescriptor().getDependencies().iterator(); j
                    .hasNext(); )
                {
                    ComponentDependency cd = (ComponentDependency) j.next();

                    artifactsToResolve.add( createArtifact( cd ) );
                }

                ArtifactResolutionResult result = artifactResolver.resolveTransitively( artifactsToResolve,
                    remoteRepositories, localRepository, sourceReader );

                for ( Iterator k = result.getArtifacts().values().iterator(); k.hasNext(); )
                {
                    Artifact a = (Artifact) k.next();

                    boolean include = true;

                    for ( int b = 0; b < artifactExcludes.length; b++ )
                    {
                        if ( a.getArtifactId().equals( artifactExcludes[b] ) )
                        {
                            include = false;

                            break;
                        }
                    }

                    if ( include )
                    {
                        componentRealm.addConstituent( a.getFile().toURL() );
                    }
                }
            }
        }
    }
}
