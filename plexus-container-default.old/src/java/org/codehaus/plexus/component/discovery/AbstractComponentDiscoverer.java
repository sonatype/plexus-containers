package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractComponentDiscoverer
    implements ComponentDiscoverer
{
    private ComponentDiscovererManager manager;

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

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

                for ( Iterator i = createComponentDescriptors( new StringReader( s ) ).iterator(); i.hasNext(); )
                {
                    ComponentDescriptor componentDescriptor = (ComponentDescriptor) i.next();

                    ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentDescriptor, getComponentType() );

                    manager.fireComponentDiscoveryEvent( event );

                    componentDescriptors.add( componentDescriptor );
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
