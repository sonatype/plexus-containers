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
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.ReaderFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Collections;

public class PlexusXmlComponentDiscoverer
    implements ComponentDiscoverer
{

    private static final String PLEXUS_XML_RESOURCE = "META-INF/plexus/plexus.xml";

    private ComponentDiscovererManager manager;

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List<ComponentSetDescriptor> findComponents( Context context, ClassRealm realm )
        throws PlexusConfigurationException
    {
        // Load the PlexusConfiguration
        PlexusConfiguration configuration = discoverConfiguration( context, realm );

        // Create ComponentDescriptors defined in PlexusConfiguration
        ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( configuration, realm );

        // Fire the event
        ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentSetDescriptor );
        manager.fireComponentDiscoveryEvent( event );

        return Collections.singletonList( componentSetDescriptor );
    }

    public PlexusConfiguration discoverConfiguration( Context context, ClassRealm realm )
        throws PlexusConfigurationException
    {
        Enumeration<URL> resources;
        try
        {
            // We don't always want to scan parent realms. For plexus
            // testcase, most components are in the root classloader so that needs to be scanned,
            // but for child realms, we don't.
            if ( realm.getParentRealm() != null )
            {
                resources = realm.findRealmResources( PLEXUS_XML_RESOURCE );
            }
            else
            {
                resources = realm.findResources( PLEXUS_XML_RESOURCE );
            }            
        }
        catch ( IOException e )
        {
            throw new PlexusConfigurationException( "Error retrieving configuration resources: " + PLEXUS_XML_RESOURCE + " from class realm: " + realm.getId(), e );
        }

        PlexusConfiguration configuration = null;
        for ( URL url : Collections.list( resources ) )
        {
            Reader reader = null;
            try
            {
                reader = ReaderFactory.newXmlReader( url.openStream() );

                ContextMapAdapter contextAdapter = new ContextMapAdapter( context );

                InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader( reader,
                                                                                                     contextAdapter );

                PlexusConfiguration discoveredConfig = PlexusTools.buildConfiguration( url.toExternalForm(), interpolationFilterReader );

                if ( configuration == null )
                {
                    configuration = discoveredConfig;
                }
                else
                {
                    configuration = PlexusConfigurationMerger.merge( configuration, discoveredConfig );
                }
            }
            catch ( IOException ex )
            {
                throw new PlexusConfigurationException( "Error reading configuration from: " + url.toExternalForm(), ex );
            }
            finally
            {
                IOUtil.close( reader );
            }
        }

        return configuration;
    }

    private ComponentSetDescriptor createComponentDescriptors( PlexusConfiguration configuration, ClassRealm realm )
        throws PlexusConfigurationException
    {
        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        if ( configuration != null )
        {
            PlexusConfiguration[] componentConfigurations = configuration.getChild( "components" ).getChildren(
                "component" );

            for ( PlexusConfiguration componentConfiguration : componentConfigurations )
            {
                ComponentDescriptor<?> componentDescriptor;
                try
                {
                    componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration );
                }
                catch ( PlexusConfigurationException e )
                {
                    throw new PlexusConfigurationException(
                        "Cannot build component descriptor from resource found in:\n" +
                            Arrays.asList( realm.getURLs() ), e );
                }

                componentDescriptor.setComponentType( "plexus" );

                componentDescriptor.setRealm( realm );

                componentDescriptor.setComponentSetDescriptor( componentSetDescriptor );

                componentSetDescriptor.addComponentDescriptor( componentDescriptor );
            }

            // TODO: read and store the dependencies
        }

        return componentSetDescriptor;
    }

}
