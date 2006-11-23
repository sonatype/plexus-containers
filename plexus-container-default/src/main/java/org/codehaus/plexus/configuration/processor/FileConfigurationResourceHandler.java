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
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class FileConfigurationResourceHandler
    extends AbstractConfigurationResourceHandler
{
    public String getId()
    {
        return "file-configuration-resource";
    }

    public PlexusConfiguration[] handleRequest( Map parameters )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        File f = new File( getSource( parameters ) );

        if ( !f.exists() )
        {
            throw new ConfigurationResourceNotFoundException( "The specified resource " + f + " cannot be found." );
        }

        FileReader configurationReader = null;
        try
        {
            configurationReader = new FileReader( f );
            return new PlexusConfiguration[]{ PlexusTools.buildConfiguration( f.getAbsolutePath(), configurationReader ) };
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ConfigurationProcessingException( e );
        }
        catch ( FileNotFoundException e )
        {
            throw new ConfigurationProcessingException( e );
        }
        finally
        {
            IOUtil.close( configurationReader );
        }
    }
}
