package org.codehaus.plexus.maven.plugin;

/*
 * The MIT License
 * 
 * Copyright (c) 2004-2006, The Codehaus
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.metadata.ClassComponentDescriptorExtractor;
import org.codehaus.plexus.metadata.ComponentDescriptorExtractor;
import org.codehaus.plexus.metadata.ComponentDescriptorWriter;
import org.codehaus.plexus.metadata.SourceComponentDescriptorExtractor;
import org.codehaus.plexus.metadata.gleaner.AnnotationComponentGleaner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @since 1.3.4
 */
public abstract class AbstractDescriptorMojo
    extends AbstractMojo
{
    protected static final String COMPILE_SCOPE = "compile";

    protected static final String TEST_SCOPE = "test";

    /**
     * The relative path to the output file for the generated metadata.
     * 
     * @parameter default-value="META-INF/plexus/components.xml"
     * @required
     */
    protected String fileName;

    /**
     * The character encoding to use when reading the source files.
     * 
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    /**
     * Whether to generate a Plexus Container descriptor instead of a component descriptor.
     * 
     * @parameter default-value="false"
     * @required
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * @parameter
     */
    private ComponentDescriptor[] roleDefaults;

    /**
     * @parameter
     */
    private ComponentDescriptorExtractor[] extractors;

    /**
     * @component
     */
    private ComponentDescriptorWriter writer;

    /**
     * @component
     */
    private MavenProjectHelper mavenProjectHelper;

    protected MavenProject getMavenProject()
    {
        return mavenProject;
    }

    protected MavenProjectHelper getMavenProjectHelper()
    {
        return mavenProjectHelper;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    protected void generateDescriptor( final String scope, final File outputFile )
        throws MojoExecutionException
    {
        assert scope != null;
        assert outputFile != null;

        // If no extractors are configured then use a default (javadoc-style source extraction)
        if ( extractors == null || extractors.length == 0 )
        {
            extractors = new ComponentDescriptorExtractor[] { new SourceComponentDescriptorExtractor( encoding ), new ClassComponentDescriptorExtractor( new AnnotationComponentGleaner() ) };
        }

        List descriptors = new ArrayList();

        for ( int i = 0; i < extractors.length; i++ )
        {
            getLog().debug( "Using extractor: " + extractors[i] );

            try
            {
                List list = extractors[i].extract( getMavenProject(), scope, roleDefaults );
                if ( list != null && !list.isEmpty() )
                {
                    descriptors.addAll( list );
                }
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Failed to extract descriptors", e );
            }
        }

        if ( descriptors.size() == 0 )
        {
            getLog().debug( "No components found" );
        }
        else
        {
            getLog().info( "Discovered " + descriptors.size() + " component descriptors(s)" );

            ComponentSetDescriptor set = new ComponentSetDescriptor();
            set.setComponents( descriptors );
            set.setDependencies( Collections.EMPTY_LIST );

            try
            {
                writeDescriptor( set, outputFile );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( "Failed to write output file", e );
            }
        }
    }

    private void writeDescriptor( final ComponentSetDescriptor desc, final File outputFile )
        throws Exception
    {
        assert desc != null;
        assert outputFile != null;

        FileUtils.forceMkdir( outputFile.getParentFile() );

        BufferedWriter output =
            new BufferedWriter( new OutputStreamWriter( new FileOutputStream( outputFile ), "UTF-8" ) );

        try
        {
            writer.writeDescriptorSet( output, desc, containerDescriptor );
            output.flush();
        }
        finally
        {
            IOUtil.close( output );
        }

        getLog().debug( "Wrote: " + outputFile );
    }
}
