package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentDiscoverer
    implements ComponentDiscoverer
{
    public List findComponents( ClassLoader classLoader )
    {
        if ( classLoader == null )
        {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        List componentDescriptors = new ArrayList();

        try
        {
            for ( Enumeration e = classLoader.getResources( getComponentDescriptorLocation() ); e.hasMoreElements(); )
            {
                URL url = (URL) e.nextElement();

                InputStream is = url.openStream();

                byte[] buffer = new byte[1024];

                int read = 0;

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                while ( is.available() > 0 )
                {
                    read = is.read( buffer, 0, buffer.length );

                    if ( read < 0 )
                    {
                        break;
                    }

                    os.write( buffer, 0, read );
                }

                is.close();

                String s = os.toString();

                PlexusConfiguration configuration = PlexusTools.buildConfiguration( s );

                for ( Iterator i = createComponentDescriptors( configuration ).iterator(); i.hasNext(); )
                {
                    componentDescriptors.add( i.next() );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return componentDescriptors;
    }
}
