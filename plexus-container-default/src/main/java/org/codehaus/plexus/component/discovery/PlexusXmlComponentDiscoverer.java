package org.codehaus.plexus.component.discovery;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.Reader;
import java.util.Arrays;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

public class PlexusXmlComponentDiscoverer
    extends AbstractResourceBasedComponentDiscoverer
{
    public String getComponentDescriptorLocation()
    {
        return "META-INF/plexus/plexus.xml";
    }

    @Override
    protected ComponentSetDescriptor createComponentDescriptors( Reader reader, String source, ClassRealm realm )
        throws PlexusConfigurationException
    {
        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( source, reader );

        if ( configuration != null )
        {
            PlexusConfiguration[] componentConfigurations = configuration.getChild( "components" ).getChildren( "component" );

            for ( PlexusConfiguration componentConfiguration : componentConfigurations )
            {
                ComponentDescriptor<?> componentDescriptor;
                
                try
                {
                    componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration, realm );

                    if ( componentDescriptor == null )
                    {
                        continue;
                    }
                }
                catch ( PlexusConfigurationException e )
                {
                    throw new PlexusConfigurationException( "Cannot build component descriptor from resource found in:\n" + Arrays.asList( realm.getURLs() ), e );
                }

                componentDescriptor.setComponentType( "plexus" );

                componentDescriptor.setComponentSetDescriptor( componentSetDescriptor );

                componentSetDescriptor.addComponentDescriptor( componentDescriptor );
            }
        }

        return componentSetDescriptor;
    }
}
