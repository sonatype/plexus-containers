package org.codehaus.plexus.component.discovery;

import org.codehaus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
        throws PlexusConfigurationException;

    // ----------------------------------------------------------------------
    //  ComponentDiscoverer
    // ----------------------------------------------------------------------

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List findComponents( Context context, ClassRealm classRealm )
        throws PlexusConfigurationException
    {
        List componentSetDescriptors = new ArrayList();

        Enumeration resources;
        try
        {
            resources = classRealm.findResources( getComponentDescriptorLocation() );
        }
        catch ( IOException e )
        {
            throw new PlexusConfigurationException( "Unable to retrieve resources for: " +
                getComponentDescriptorLocation() + " in class realm: " + classRealm.getId() );
        }
        for ( Enumeration e = resources; e.hasMoreElements(); )
        {
            URL url = (URL) e.nextElement();

            InputStreamReader reader = null;
            try
            {
                URLConnection conn = url.openConnection();
                conn.setUseCaches( false );
                conn.connect();

                reader = new InputStreamReader( conn.getInputStream() );
                InterpolationFilterReader input = new InterpolationFilterReader( reader,
                                                                                 new ContextMapAdapter( context ) );

                ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( input, url.toString() );

                componentSetDescriptors.add( componentSetDescriptor );

                // Fire the event
                ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentSetDescriptor );

                manager.fireComponentDiscoveryEvent( event );
            }
            catch ( IOException ex )
            {
                throw new PlexusConfigurationException( "Error reading configuration " + url, ex );
            }
            finally
            {
                IOUtil.close( reader );
            }
        }

        return componentSetDescriptors;
    }
}
