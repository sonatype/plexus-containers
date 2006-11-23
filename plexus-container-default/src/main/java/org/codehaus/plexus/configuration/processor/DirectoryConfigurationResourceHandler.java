package org.codehaus.plexus.configuration.processor;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DirectoryConfigurationResourceHandler
    extends AbstractConfigurationResourceHandler
{
    public String getId()
    {
        return "directory-configuration-resource";
    }

    public PlexusConfiguration[] handleRequest( Map parameters )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        File f = new File( getSource( parameters ) );

        if ( !f.exists() )
        {
            throw new ConfigurationResourceNotFoundException( "The specified resource " + f + " cannot be found." );
        }

        if ( !f.isDirectory() )
        {
            throw new ConfigurationResourceNotFoundException( "The specified resource " + f + " is not a directory." );
        }

        // ----------------------------------------------------------------------
        // Parameters
        //
        // source == basedir
        // includes
        // excludes
        // ----------------------------------------------------------------------

        String includes = (String) parameters.get( "includes" );

        if ( includes == null )
        {
            includes = "**/*.xml";
        }

        String excludes = (String) parameters.get( "excludes" );

        try
        {
            List files = FileUtils.getFiles( f, includes, excludes );

            // ----------------------------------------------------------------------
            // For each file we find we want to read it in and turn it into a
            // PlexusConfiguration and insert it into the source configuration.
            // ----------------------------------------------------------------------

            PlexusConfiguration[] configurations = new PlexusConfiguration[files.size()];

            for ( int i = 0; i < configurations.length; i++ )
            {
                File configurationFile = (File) files.get( i );

                PlexusConfiguration configuration = PlexusTools.buildConfiguration( configurationFile.getAbsolutePath(), new FileReader( configurationFile ) );

                configurations[i] = configuration;
            }

            return configurations;
        }
        catch ( FileNotFoundException e )
        {
            throw new ConfigurationProcessingException( e );
        }
        catch ( IOException e )
        {
            throw new ConfigurationProcessingException( e );
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationProcessingException( e );
        }
    }
}
