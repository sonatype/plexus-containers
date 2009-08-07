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

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.ReaderFactory;

//TODO: this should be a default strategy of searching through classloaders. a discoverer should really not have to be
// concerned finding a particular resource and how to turn it into a set of component descriptors.

/**
 * @author Jason van Zyl
 */
public abstract class AbstractResourceBasedComponentDiscoverer
    implements ComponentDiscoverer
{
    protected abstract String getComponentDescriptorLocation();

    protected abstract ComponentSetDescriptor createComponentDescriptors( Reader reader, String source, ClassRealm realm )
        throws PlexusConfigurationException;

    public List<ComponentSetDescriptor> findComponents( Context context, ClassRealm realm )
        throws PlexusConfigurationException
    {
        List<ComponentSetDescriptor> componentSetDescriptors = new ArrayList<ComponentSetDescriptor>();

        Enumeration<URL> resources;
        
        try
        {
            resources = realm.getResources( getComponentDescriptorLocation() );
        }
        catch ( IOException e )
        {
            throw new PlexusConfigurationException( "Unable to retrieve resources for: " + getComponentDescriptorLocation() + " in class realm: " + realm.getId() );
        }

        for ( URL url : Collections.list( resources ))
        {
            Reader reader = null;
            
            try
            {
                URLConnection conn = url.openConnection();

                conn.setUseCaches( false );

                conn.connect();

                reader = ReaderFactory.newXmlReader( conn.getInputStream() );

                InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader( reader, new ContextMapAdapter( context ) );

                ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( interpolationFilterReader, url.toString(), realm );

                componentSetDescriptors.add( componentSetDescriptor );
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
