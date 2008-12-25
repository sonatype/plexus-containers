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
 * Generates a Plexus <tt>components.xml</tt> component descriptor file from source (javadoc) or
 * class annotations.
 * 
 * @goal generate-metadata
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @author Jason van Zyl
 * @author Trygve Laugst&oslash;l
 * @version $Id$
 */
public class PlexusDescriptorMojo
    extends AbstractDescriptorMojo
{
    /**
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    protected File generatedMetadata;

    /**
     * @parameter default-value="${basedir}/src/main/resources/META-INF/plexus"
     * @required
     */
    protected File staticMetadataDirectory;

    /**
     * @parameter default-value="${project.build.directory}/components.xml"
     * @required
     */
    protected File intermediaryMetadata;

    public void execute()
        throws MojoExecutionException
    {
        MetadataGenerationRequest request = new MetadataGenerationRequest();

        try
        {
            request.classpath = mavenProject.getCompileClasspathElements();
            request.classesDirectory = new File( mavenProject.getBuild().getOutputDirectory() );
            request.sourceDirectories = mavenProject.getCompileSourceRoots();
            request.sourceEncoding = sourceEncoding;
            request.componentDescriptorDirectory = staticMetadataDirectory;
            request.intermediaryFile = intermediaryMetadata;
            request.outputFile = generatedMetadata;
            
            metadataGenerator.generateDescriptor( request );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error generating metadata: ", e );
        }
    }
}
