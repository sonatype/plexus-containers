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

package org.codehaus.plexus.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.metadata.gleaner.QDoxComponentGleaner;
import org.codehaus.plexus.metadata.gleaner.SourceComponentGleaner;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Extracts {@link ComponentDescriptor} from source files.
 * 
 * @version $Rev$ $Date$
 */
public class SourceComponentDescriptorExtractor
    extends ComponentDescriptorExtractorSupport
{
    private SourceComponentGleaner gleaner;

    public SourceComponentDescriptorExtractor( final SourceComponentGleaner gleaner )
    {
        this.gleaner = gleaner;
    }

    public SourceComponentDescriptorExtractor()
    {
    }

    public List extract( final MavenProject project, final String scope, final ComponentDescriptor[] roleDefaults )
        throws Exception
    {
        assert project != null;
        assert scope != null;
        // getDefaultsByRole() seems to check for null and maven-artifact project works fine when
        // assertions are disabled.        
        //        assert roleDefaults != null;

        // Use a default source gleaner if none was configured
        if ( gleaner == null )
        {
            gleaner = new QDoxComponentGleaner();
        }

        List roots;

        if ( COMPILE_SCOPE.equals( scope ) )
        {
            roots = project.getCompileSourceRoots();
        }
        else if ( TEST_SCOPE.equals( scope ) )
        {
            roots = project.getTestCompileSourceRoots();
        }
        else
        {
            throw new IllegalArgumentException( "Invalid scope: " + scope );
        }

        return extract( roots, getDefaultsByRole( roleDefaults ) );
    }

    private List extract( final List sourceDirectories, final Map defaultsByRole )
        throws Exception
    {
        assert sourceDirectories != null;
        assert defaultsByRole != null;

        List descriptors = new ArrayList();

        // Scan the sources
        JavaDocBuilder builder = new JavaDocBuilder();

        for ( Iterator iter = sourceDirectories.iterator(); iter.hasNext(); )
        {
            File dir = new File( (String) iter.next() );

            //getLogger().debug( "Adding source directory: " + dir );

            builder.addSourceTree( dir );
        }

        JavaClass[] classes = builder.getClasses();

        // For each class we find, try to glean off a descriptor
        for ( int i = 0; i < classes.length; i++ )
        {
            //getLogger().debug( "Gleaning from: " + classes[i].getFullyQualifiedName() );

            ComponentDescriptor descriptor = gleaner.glean( builder, classes[i] );

            if ( descriptor != null )
            {
                applyDefaults( descriptor, defaultsByRole );

                descriptors.add( descriptor );
            }
        }

        return descriptors;
    }
}