/*
 * Copyright (C) 2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codehaus.plexus.metadata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.metadata.gleaner.AnnotationComponentGleaner;
import org.codehaus.plexus.metadata.gleaner.ClassComponentGleaner;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Extracts {@link ComponentDescriptor} from class files.
 * 
 * @version $Id$
 */
public class ClassComponentDescriptorExtractor
    extends ComponentDescriptorExtractorSupport
{
    private ClassComponentGleaner gleaner;

    public ClassComponentDescriptorExtractor( final ClassComponentGleaner gleaner )
    {
        this.gleaner = gleaner;
    }

    public ClassComponentDescriptorExtractor()
    {
        this.gleaner = new AnnotationComponentGleaner();
    }

    public List extract( ExtractorConfiguration configuration, final ComponentDescriptor[] roleDefaults )
        throws Exception
    {
        assert roleDefaults != null;

        // We don't have a reasonable default to use, so just puke up
        if ( gleaner == null )
        {
            throw new IllegalStateException( "Gleaner is not bound" );
        }

        if ( !configuration.classesDirectory.exists() )
        {
            //getLogger().warn( "Missing classes directory: " + classesDir );
            return Collections.EMPTY_LIST;
        }

        if ( configuration.useContextClassLoader )
        {
            return extract( configuration.classesDirectory, Thread.currentThread().getContextClassLoader(), getDefaultsByRole( roleDefaults ) );
        }
        else
        {
            ClassLoader prev = Thread.currentThread().getContextClassLoader();
            ClassLoader cl = createClassLoader( configuration.classpath );

            Thread.currentThread().setContextClassLoader( cl );

            try
            {
                return extract( configuration.classesDirectory, cl, getDefaultsByRole( roleDefaults ) );
            }
            finally
            {
                Thread.currentThread().setContextClassLoader( prev );
            }
        }
    }

    // XXX replace ClassLoader with own abstraction for getting class files
    private ClassLoader createClassLoader( final List elements )
        throws Exception
    {
        List list = new ArrayList();

        // Add the projects dependencies
        for ( Iterator iter = elements.iterator(); iter.hasNext(); )
        {
            String filename = (String) iter.next();

            try
            {
                list.add( new File( filename ).toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Invalid classpath entry: " + filename, e );
            }
        }

        URL[] urls = (URL[]) list.toArray( new URL[list.size()] );

        //getLogger().debug( "Classpath:" );
        for ( int i = 0; i < urls.length; i++ )
        {
            //getLogger().debug( "    " + urls[i] );
        }

        return new URLClassLoader( urls, getClass().getClassLoader() );
    }

    private List extract( final File classesDir, final ClassLoader cl, final Map defaultsByRole )
        throws Exception
    {
        assert classesDir != null;
        assert cl != null;
        assert defaultsByRole != null;

        List descriptors = new ArrayList();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir( classesDir );
        scanner.addDefaultExcludes();
        scanner.setIncludes( new String[] { "**/*.class" } );

        //getLogger().debug( "Scanning for classes in: " + classesDir );

        scanner.scan();

        String[] includes = scanner.getIncludedFiles();

        for ( String include : includes )
        {
            String className = include.substring( 0, include.lastIndexOf( ".class" ) ).replace( '\\', '.' ).replace( '/', '.' );

            try
            {
                // Class type = cl.loadClass( className );

                ComponentDescriptor descriptor = gleaner.glean( className, cl );

                if ( descriptor != null )
                {
                    applyDefaults( descriptor, defaultsByRole );

                    descriptors.add( descriptor );
                }
            }
            catch ( VerifyError e )
            {
                // getLogger().error( "Failed to load class: " + className + "; cause: " + e );
            }
        }

        return descriptors;
    }
}