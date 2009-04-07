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

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason van Zyl
 */
public class DefaultComponentDiscoverer
    extends AbstractResourceBasedComponentDiscoverer
{
    public String getComponentDescriptorLocation()
    {
        return "META-INF/plexus/components.xml";
    }

    public ComponentSetDescriptor createComponentDescriptors( Reader componentDescriptorReader, String source, ClassRealm realm )
        throws PlexusConfigurationException
    {
        PlexusConfiguration componentDescriptorConfiguration = PlexusTools.buildConfiguration( source, componentDescriptorReader );

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        List<ComponentDescriptor<?>> componentDescriptors = new ArrayList<ComponentDescriptor<?>>();

        PlexusConfiguration[] componentConfigurations = componentDescriptorConfiguration.getChild( "components" ).getChildren( "component" );

        for ( PlexusConfiguration componentConfiguration : componentConfigurations )
        {
            ComponentDescriptor<?> componentDescriptor;
            try
            {
                componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration, realm );
            }
            catch ( PlexusConfigurationException e )
            {
               	// This is not the most accurate of exceptions as the only real case where this exception
            	// will be thrown is when the implementation class of the component sited cannot be loaded.
            	// In the case where role and implementation classes do not exist then we just shouldn't
            	// create the component descriptor. All information should be taken from annotations which
            	// will be correct, so in the case we can't load the class it must be coming from and older
            	// hand written descriptor which is incorrect.
            	
            	continue;            	
            }

            componentDescriptor.setSource( source );

            componentDescriptor.setComponentType( "plexus" );

            componentDescriptor.setComponentSetDescriptor( componentSetDescriptor );

            componentDescriptors.add( componentDescriptor );
        }

        componentSetDescriptor.setComponents( componentDescriptors );

        componentSetDescriptor.setSource( source );

        return componentSetDescriptor;
    }
}
