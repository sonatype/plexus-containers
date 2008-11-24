package org.codehaus.plexus.metadata;

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

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.metadata.gleaner.AnnotationComponentGleaner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * @author Jason van Zyl
 */
public class DefaultMetadataGenerator
    extends AbstractLogEnabled
    implements MetadataGenerator
{
    private ComponentDescriptor[] roleDefaults;
    private ComponentDescriptorExtractor[] extractors;
    private ComponentDescriptorWriter writer = new DefaultComponentDescriptorWriter();

    public void generateDescriptor( ExtractorConfiguration configuration, File outputFile )
        throws Exception
    {
        assert outputFile != null;
        
        if ( extractors == null || extractors.length == 0 )
        {
            extractors = new ComponentDescriptorExtractor[] { new SourceComponentDescriptorExtractor(), new ClassComponentDescriptorExtractor( new AnnotationComponentGleaner() ) };
        }

        List descriptors = new ArrayList();

        for ( int i = 0; i < extractors.length; i++ )
        {
            getLogger().debug( "Using extractor: " + extractors[i] );

            try
            {
                List list = extractors[i].extract( configuration, roleDefaults );
                if ( list != null && !list.isEmpty() )
                {
                    descriptors.addAll( list );
                }
            }
            catch ( Exception e )
            {
                throw new Exception( "Failed to extract descriptors", e );
            }
        }

        if ( descriptors.size() == 0 )
        {
            getLogger().debug( "No components found" );
        }
        else
        {
            getLogger().info( "Discovered " + descriptors.size() + " component descriptors(s)" );

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

    private void writeDescriptor( ComponentSetDescriptor desc, File outputFile )
        throws Exception
    {        
        assert desc != null;
        assert outputFile != null;

        FileUtils.forceMkdir( outputFile.getParentFile() );
        
        BufferedWriter output =
            new BufferedWriter( new OutputStreamWriter( new FileOutputStream( outputFile ), "UTF-8" ) );

        try
        {
            writer.writeDescriptorSet( output, desc, false );
            output.flush();
        }
        finally
        {
            IOUtil.close( output );
        }

        getLogger().debug( "Wrote: " + outputFile );
    }
}
