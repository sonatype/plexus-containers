package org.codehaus.plexus.component.discovery;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.classworlds.ClassRealm;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractComponentDiscoverer
    implements ComponentDiscoverer
{
    private ComponentDiscovererManager manager;

    // ----------------------------------------------------------------------
    //  Abstract methods
    // ----------------------------------------------------------------------

    protected abstract String getComponentDescriptorLocation();

    protected abstract ComponentSetDescriptor createComponentDescriptors( Reader reader, String source )
        throws Exception;

    // ----------------------------------------------------------------------
    //  ComponentDiscoverer
    // ----------------------------------------------------------------------

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List findComponents( ClassRealm classRealm )
    {
        List componentSetDescriptors = new ArrayList();

        try
        {
            for ( Enumeration e = classRealm.findResources( getComponentDescriptorLocation() ); e.hasMoreElements(); )
            {
                URL url = (URL) e.nextElement();

                String descriptor = IOUtil.toString( url.openStream() );

                ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( new StringReader( descriptor ), url.toString() );

                componentSetDescriptors.add( componentSetDescriptor );

                // Fire the event
                ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentSetDescriptor );

                manager.fireComponentDiscoveryEvent( event );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return componentSetDescriptors;
    }
}
