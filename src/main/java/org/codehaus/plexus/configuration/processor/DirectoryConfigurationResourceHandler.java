package org.codehaus.plexus.configuration.processor;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
