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
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.metadata.merge.Merger;

/**
 * Merges a set of Plexus descriptors into one descriptor file.
 * 
 * @goal merge-metadata
 * @phase process-classes
 * @author Jason van Zyl
 * @author Trygve Laugst&oslash;l
 * @version $Id$
 */
public class PlexusMergeMojo
    extends AbstractMojo
{
    /**
     * The destination for the merged descriptor.
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    private File output;

    /**
     * The paths of the input descriptors to merge.
     * 
     * @parameter
     */
    private File[] descriptors;

    /** @component role-hint="componentsXml" */
    private Merger merger;

    public void execute()
        throws MojoExecutionException
    {
        List<File> files = new ArrayList<File>();

        if ( descriptors != null )
        {
            files.addAll( Arrays.asList( descriptors ) );
        }

        if ( files.isEmpty() )
        {
            return;
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
