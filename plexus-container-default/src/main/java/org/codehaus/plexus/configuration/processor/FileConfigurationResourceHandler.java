package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Map;

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
