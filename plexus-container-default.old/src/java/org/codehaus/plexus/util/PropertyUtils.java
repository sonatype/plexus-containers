package org.codehaus.plexus.util;

import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PropertyUtils
{
    public static Properties loadProperties( File file )
    {
        try
        {
            return loadProperties( new FileInputStream( file ) );
        }
        catch ( Exception e )
        {
            // ignore
        }

        return null;
    }

    public static Properties loadProperties( InputStream is )
    {
        try
        {
            Properties properties = new Properties();

            // Make sure the properties stream is valid
            if ( is != null )
            {
                properties.load( is );
            }

            return properties;
        }
        catch ( IOException e )
        {
            // ignore
        }
        finally
        {
            try
            {
                if ( is != null )
                {
                    is.close();
                }
            }
            catch ( IOException e )
            {
                // ignore
            }
        }

        return null;
    }
}
