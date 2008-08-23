package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004-2005, Codehaus.org
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.util.Collections;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generates a Plexus <tt>components.xml</tt> component descriptor file from test source (javadoc)
 * or test class annotations.
 * 
 * @goal generate-test-metadata
 * @phase process-test-classes
 * @requiresDependencyResolution test
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @since 1.3.4
 */
public class PlexusTestDescriptorMojo
    extends AbstractDescriptorMojo
{
    //
    // FIXME: When running as process-classes, which is required to deal with the class-based annotation gleaning
    //        we can't put the generated file into resources, as it will never make it to the correct directory
    //
    //        expression="${project.build.directory}/generated-test-resources/plexus/"
    //

    /**
     * The output directory where the descriptor is written.
     * 
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        // Only execute if the current project looks like its got Java bits in it
        ArtifactHandler artifactHandler = getMavenProject().getArtifact().getArtifactHandler();

        if ( !"java".equals( artifactHandler.getLanguage() ) )
        {
            getLog().debug( "Not executing on non-Java project" );
        }
        else
        {
            generateDescriptor( TEST_SCOPE, new File( outputDirectory, fileName ) );

            getMavenProjectHelper().addTestResource( getMavenProject(), outputDirectory.getAbsolutePath(), Collections.EMPTY_LIST, Collections.EMPTY_LIST );
        }
    }
}
