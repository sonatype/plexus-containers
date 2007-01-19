package org.codehaus.plexus.component.discovery;

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

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
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
import java.util.Iterator;

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

    protected abstract ComponentSetDescriptor createComponentDescriptors( Reader reader,
                                                                          String source )
        throws PlexusConfigurationException;

    // ----------------------------------------------------------------------
    //  ComponentDiscoverer
    // ----------------------------------------------------------------------

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List findComponents( Context context,
                                ClassRealm classRealm )
        throws PlexusConfigurationException
    {
        List componentSetDescriptors = new ArrayList();

        Enumeration resources;
        try
        {
            // We don't always want to scan parent realms. For plexus
            // testcase, most components are in the root classloader so that needs to be scanned,
            // but for child realms, we don't.
            if ( classRealm.getParentRealm() != null )
                resources = classRealm.findRealmResources( getComponentDescriptorLocation() );
            else
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

                InterpolationFilterReader interpolationFilterReader =
                    new InterpolationFilterReader( reader, new ContextMapAdapter( context ) );

                ComponentSetDescriptor componentSetDescriptor =
                    createComponentDescriptors( interpolationFilterReader, url.toString() );

                if ( componentSetDescriptor.getComponents() != null )
                {
                    for ( Iterator i = componentSetDescriptor.getComponents().iterator(); i.hasNext(); )
                    {
                        ComponentDescriptor cd = (ComponentDescriptor) i.next();

                        cd.setRealmId( classRealm.getId() );
                    }
                }

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
