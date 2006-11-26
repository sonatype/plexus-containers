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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultComponentDiscoverer
    extends AbstractComponentDiscoverer
{
    public String getComponentDescriptorLocation()
    {
        return "META-INF/plexus/components.xml";
    }

    public ComponentSetDescriptor createComponentDescriptors( Reader componentDescriptorReader, String source )
        throws PlexusConfigurationException
    {
        PlexusConfiguration componentDescriptorConfiguration = PlexusTools.buildConfiguration( source, componentDescriptorReader );

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        List componentDescriptors = new ArrayList();

        PlexusConfiguration[] componentConfigurations =
            componentDescriptorConfiguration.getChild( "components" ).getChildren( "component" );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            PlexusConfiguration componentConfiguration = componentConfigurations[i];

            ComponentDescriptor componentDescriptor;

            try
            {
                componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration );
            }
            catch ( PlexusConfigurationException e )
            {
                throw new PlexusConfigurationException( "Cannot process component descriptor: " + source, e );
            }

            componentDescriptor.setComponentType( "plexus" );

            componentDescriptors.add( componentDescriptor );
        }

        componentSetDescriptor.setComponents( componentDescriptors );

        // TODO: read and store the dependencies

        return componentSetDescriptor;
    }
}
