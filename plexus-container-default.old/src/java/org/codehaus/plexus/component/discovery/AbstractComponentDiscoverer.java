package org.codehaus.plexus.component.discovery;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;

public abstract class AbstractComponentDiscoverer
    implements ComponentDiscoverer
{
    private ComponentDiscovererManager manager;

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public ComponentSetDescriptor findComponents( ClassLoader classLoader )
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

                ComponentSetDescriptor componentSet = createComponentDescriptors( new StringReader( s ), url.toString() );

                for ( Iterator i = componentSet.getComponents().iterator(); i.hasNext(); )
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

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        componentSetDescriptor.setComponents( componentDescriptors );

        return componentSetDescriptor;
    }

    protected abstract String getComponentDescriptorLocation();

    protected abstract String getComponentType();

    protected abstract ComponentSetDescriptor createComponentDescriptors( Reader reader, String source )
        throws Exception;
}
