package org.codehaus.plexus.component.discovery;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.io.Reader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentDiscoverer
    extends AbstractComponentDiscoverer
{
    public String getComponentDescriptorLocation()
    {
        return "META-INF/plexus/components.xml";
    }

    public String getComponentType()
    {
        return "plexus";
    }

    public List createComponentDescriptors( Reader componentDescriptorReader, String source )
        throws Exception
    {
        PlexusConfiguration componentDescriptorConfiguration = PlexusTools.buildConfiguration( componentDescriptorReader );

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

            componentDescriptors.add( componentDescriptor );
        }

        return componentDescriptors;
    }
}
