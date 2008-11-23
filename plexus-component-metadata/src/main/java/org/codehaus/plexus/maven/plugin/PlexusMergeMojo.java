package org.codehaus.plexus.maven.plugin;

/*
 * Copyright (c) 2004-2006, Codehaus.org
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.metadata.merge.Merger;
import org.codehaus.plexus.util.FileUtils;

/**
 * @goal merge-metadata
 * @phase process-classes
 * @description Merges all Plexus descriptors in the main sources.
 * 
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusMergeMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project.resources}"
     * @required
     */
    private List resources;

    /**
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    private File output;

    /**
     * @parameter
     */
    private File[] descriptors;

    /** @component */
    private Merger merger;

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        // Locate files
        // ----------------------------------------------------------------------

        List files = new ArrayList();

        for ( Iterator it = resources.iterator(); it.hasNext(); )
        {
            Resource resource = (Resource) it.next();

            String includes = "META-INF/plexus/components.xml";

            String excludes = "";

            for ( Iterator j = resource.getExcludes().iterator(); j.hasNext(); )
            {
                String exclude = (String) j.next();
                excludes += exclude + ",";
            }

            try
            {
                File basedir = new File( resource.getDirectory() );

                getLog().debug( "Searching for component.xml files. Basedir: " + basedir.getAbsolutePath() + ", includes: " + includes + ", excludes=" + excludes );

                if ( !basedir.isDirectory() )
                {
                    getLog().debug( "Skipping, not a directory." );

                    continue;
                }

                List list = FileUtils.getFiles( basedir, includes, excludes );

                files.addAll( list );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error while scanning for component.xml files.", e );
            }
        }

        if ( descriptors != null )
        {
            files.addAll( Arrays.asList( descriptors ) );
        }

        // ----------------------------------------------------------------------
        // Merge the component set descriptors
        // ----------------------------------------------------------------------

        if ( files.isEmpty() )
        {
            getLog().debug( "Didn't find any files to merge." );

            return;
        }

        getLog().debug( "Found " + files.size() + " files to merge:" );

        for ( Iterator it = files.iterator(); it.hasNext(); )
        {
            File file = (File) it.next();

            getLog().debug( file.getAbsolutePath() );
        }

        try
        {
            merger.mergeDescriptors( output, files );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }
    }
}
