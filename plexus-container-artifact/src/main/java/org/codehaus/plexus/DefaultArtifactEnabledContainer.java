package org.codehaus.plexus;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author jdcasey
 * @version $Id$
 */
public class DefaultArtifactEnabledContainer
    extends DefaultPlexusContainer
    implements ArtifactEnabledContainer
{
    // TODO: should be a component?
    private ArtifactFactory artifactFactory = new DefaultArtifactFactory();

    public DefaultArtifactEnabledContainer()
    {
        super();
    }

    private Artifact createArtifact( ComponentDependency cd )
    {
        return artifactFactory.createArtifact( cd.getGroupId(), cd.getArtifactId(), cd.getVersion(),
                                               Artifact.SCOPE_RUNTIME, cd.getType() );
    }

    // ----------------------------------------------------------------------
    // Dynamic component addition
    // ----------------------------------------------------------------------

    // We could have an option here for isolation ...

    int realmTmpId = 0;

    public void addComponent( Artifact component, ArtifactResolver artifactResolver, List remoteRepositories,
                              ArtifactRepository localRepository, ArtifactMetadataSource sourceReader,
                              ArtifactFilter filter )
        throws ArtifactResolutionException, ArtifactEnabledContainerException
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

        // TODO: we are being passed in the plugin repository for this, but then later using those to resolve other artifacts.
        //   the passed in remote repos should be the artifact repositories, and this should be done before the addComponent call...
        //   (see MNG-229)
        artifactResolver.resolve( component, remoteRepositories, localRepository );

        realmTmpId++;

        ClassWorld classWorld = getClassWorld();

        List componentDescriptors = null;
        try
        {
            ClassRealm tmp = classWorld.newRealm( "tmp" + realmTmpId );

            tmp.addConstituent( getArtifactUrl( component ) );

            componentDescriptors = discoverArtifactComponents( tmp );

            classWorld.disposeRealm( "tmp" + realmTmpId );
        }
        catch ( DuplicateRealmException e )
        {
            throw new ArtifactEnabledContainerException( "Unable to add component class realm", e );
        }
        catch ( NoSuchRealmException e )
        {
            throw new ArtifactEnabledContainerException( "Unable to add component class realm", e );
        }

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

        ClassRealm componentRealm;

        ClassRealm plexusRealm = getContainerRealm();

        String realmId = component.getId();

        try
        {
            componentRealm = plexusRealm.createChildRealm( realmId );
        }
        catch ( DuplicateRealmException e )
        {
            throw new ArtifactEnabledContainerException( "Unable to add component class realm", e );
        }

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            ComponentDescriptor componentDescriptor = (ComponentDescriptor) i.next();

            String componentKey = componentDescriptor.getComponentKey();

            // Add a alias for the components in the artifact itself
            addRealmAlias( componentKey, realmId );

            if ( componentDescriptor.getComponentSetDescriptor().getDependencies() != null )
            {
                Set artifactsToResolve = new HashSet();

                for ( Iterator j = componentDescriptor.getComponentSetDescriptor().getDependencies().iterator();
                      j.hasNext(); )
                {
                    ComponentDependency cd = (ComponentDependency) j.next();

                    // ----------------------------------------------------------------------
                    // Don't even attempt to transtively resolve artifacts we are excluding.
                    // ----------------------------------------------------------------------

                    Artifact componentArtifact = createArtifact( cd );

                    if ( filter.include( componentArtifact ) )
                    {
                        artifactsToResolve.add( componentArtifact );
                    }
                }

                // ----------------------------------------------------------------------
                // NOTE!!!!
                //
                // This is the wrong way to do this I now realize. What i need to do is
                // collect the metedata about the components i.e. just pull in the
                // component descriptor and create the DAG. Then when the component is
                // requested the first time, at that point create the realm and use
                // the DAG to populate the realm correctly. This method below is
                // leading to jumbled realms and the alias mechanism used is crap and
                // won't be needed when I delay the creation of the realms using the
                // component metadata which is the way to go. jvz.
                // ----------------------------------------------------------------------

                // ----------------------------------------------------------------------
                // I need to pass the exclusion parameters into the artifact resolution
                // phase to prevent duplication entries.
                // ----------------------------------------------------------------------

                ArtifactResolutionResult result = artifactResolver.resolveTransitively( artifactsToResolve,
                                                                                        remoteRepositories,
                                                                                        localRepository, sourceReader,
                                                                                        filter );

                for ( Iterator k = result.getArtifacts().values().iterator(); k.hasNext(); )
                {
                    Artifact a = (Artifact) k.next();

                    if ( filter.include( a ) )
                    {
                        componentRealm.addConstituent( getArtifactUrl( a ) );
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
                List dependencyComponents = discoverArtifactComponents( componentRealm );

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

                    addRealmAlias( dcd.getComponentKey(), realmId );
                }

                dependencyComponentsDiscovered = true;
            }

            // ----------------------------------------------------------------------
            // Now that the dependencies have been processed we can add the component
            // JAR itself into the realm and everything is now ready for use.
            // ----------------------------------------------------------------------

            componentRealm.addConstituent( getArtifactUrl( component ) );
        }
    }

    private List discoverArtifactComponents( ClassRealm tmp )
        throws ArtifactEnabledContainerException
    {
        try
        {
            return discoverComponents( tmp );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ArtifactEnabledContainerException( "Error discovering components", e );
        }
        catch ( ComponentRepositoryException e )
        {
            throw new ArtifactEnabledContainerException( "Error discovering components", e );
        }
    }

    private URL getArtifactUrl( Artifact component )
        throws ArtifactEnabledContainerException
    {
        try
        {
            return component.getFile().toURL();
        }
        catch ( MalformedURLException e )
        {
            throw new ArtifactEnabledContainerException( "Error constructing file URL", e );
        }
    }
}
