package org.codehaus.plexus.configuration.processor;

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

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id: ResourceBundleConfigurationResourceHandler.java 3657 $
 */
public class ResourceBundleConfigurationResourceHandler
    extends AbstractConfigurationResourceHandler
{
    public String getId()
    {
        return "resourcebundle-configuration-resource";
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
