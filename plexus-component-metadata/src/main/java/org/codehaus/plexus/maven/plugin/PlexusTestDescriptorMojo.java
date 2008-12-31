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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.metadata.MetadataGenerationRequest;

/**
 * Generates a Plexus <tt>components.xml</tt> component descriptor file from test source (javadoc)
 * or test class annotations.
 * 
 * @goal generate-test-metadata
 * @phase process-test-classes
 * @requiresDependencyResolution test
 * @author Jason van Zyl
 * @author Trygve Laugst&oslash;l
 * @version $Id$
 */
public class PlexusTestDescriptorMojo
    extends AbstractDescriptorMojo
{
    /**
     * The output location for the generated descriptor.
     * 
     * @parameter default-value="${project.build.testOutputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    protected File testGeneratedMetadata;

    /**
     * The location of manually crafted component descriptors. The contents of the descriptor files in this directory is
     * merged with the information extracted from the project's sources/classes.
     * 
     * @parameter default-value="${basedir}/src/test/resources/META-INF/plexus"
     * @required
     */
    protected File testStaticMetadataDirectory;

    /**
     * The output location for the intermediary descriptor. This descriptors contains only the information extracted
     * from the project's sources/classes.
     * 
     * @parameter default-value="${project.build.directory}/test-components.xml"
     * @required
     */
    protected File testIntermediaryMetadata;

    public void execute()
        throws MojoExecutionException
    {
        MetadataGenerationRequest request = new MetadataGenerationRequest();

        try
        {
            request.classpath = mavenProject.getTestClasspathElements();
            request.classesDirectory = new File( mavenProject.getBuild().getTestOutputDirectory() );
            request.sourceDirectories = mavenProject.getTestCompileSourceRoots();
            request.sourceEncoding = sourceEncoding;
            request.componentDescriptorDirectory = testStaticMetadataDirectory;
            request.intermediaryFile = testIntermediaryMetadata;
            request.outputFile = testGeneratedMetadata;
            
            metadataGenerator.generateDescriptor( request );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error generating test metadata: ", e );
        }
    }
}
