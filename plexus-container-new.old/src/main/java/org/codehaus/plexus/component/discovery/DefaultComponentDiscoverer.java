package org.codehaus.plexus.component.discovery;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;

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
        throws Exception
    {
        PlexusConfiguration componentDescriptorConfiguration = PlexusTools.buildConfiguration( componentDescriptorReader );

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        List componentDescriptors = new ArrayList();

        PlexusConfiguration[] componentConfigurations =
            componentDescriptorConfiguration.getChild( "components" ).getChildren( "component" );

        for ( int i = 0; i < componentConfigurations.length; i++ )
        {
            PlexusConfiguration componentConfiguration = componentConfigurations[i];

            ComponentDescriptor componentDescriptor = null;

            try
            {
                componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration );
            }
            catch( Exception e )
            {
                throw new Exception( "Cannot process component descriptor: " + source, e );                
            }

            componentDescriptor.setComponentType( "plexus" );

            componentDescriptors.add( componentDescriptor );
        }

        componentSetDescriptor.setComponents( componentDescriptors );

        // TODO: read and store the dependencies

        return componentSetDescriptor;
    }
}
