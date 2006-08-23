package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.util.*;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: FileConfigurationResourceHandler.java 3329 2006-05-23 06:47:54Z jvanzyl $
 */
public class ResourceBundleConfigurationResourceHandler
    extends AbstractConfigurationResourceHandler
{
    public String getId()
    {
        return "property-configuration-resource";
    }

    public PlexusConfiguration[] handleRequest( Map parameters )
        throws ConfigurationResourceNotFoundException, ConfigurationProcessingException
    {
        String bundleName = getSource( parameters );
        ResourceBundle bundle;

        try
        {
            bundle = ResourceBundle.getBundle( bundleName );
        }
        catch ( MissingResourceException e )
        {
            throw new ConfigurationResourceNotFoundException( "The specified resource " + bundleName + " cannot be found." );
        }

        PlexusConfiguration[] ret = new PlexusConfiguration[1];
        ret[0] = new XmlPlexusConfiguration( bundleName );
        Enumeration props = bundle.getKeys();
        while (props.hasMoreElements()) {
            String prop = (String) props.nextElement();

            XmlPlexusConfiguration conf = new XmlPlexusConfiguration( prop );
            conf.setValue( bundle.getString( prop ) );

            ret[0].addChild(conf);
        }

        return ret;
    }
}
