package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentDiscoverer
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
            for ( Enumeration e = classLoader.getResources( COMPONENT_RESOURCES ); e.hasMoreElements(); )
            {
                URL url = (URL) e.nextElement();

                InputStream is = url.openStream();

                byte[] buffer = new byte[1024];

                int read = 0;

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                while( is.available() > 0 )
                {
                    read = is.read( buffer, 0, buffer.length );

                    if ( read < 0 )
                    {
                        break;
                    }

                    os.write( buffer, 0, read );
                }

                String configuration = os.toString();

                PlexusConfiguration[] componentConfigurations =
                    PlexusTools.buildConfiguration( configuration ).getChild( "components" ).getChildren( "component" );

                for ( int i = 0; i < componentConfigurations.length; i++ )
                {
                    PlexusConfiguration c = componentConfigurations[i];

                    ComponentDescriptor cd = PlexusTools.buildComponentDescriptor( c );

                    componentDescriptors.add( cd );
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
