package org.codehaus.plexus.component.configurator;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.io.StringReader;
import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentConfiguratorTest extends AbstractComponentConfiguratorTest
{
    public DefaultComponentConfiguratorTest( String s )
    {
        super( s );
    }

    protected ComponentConfigurator getComponentConfigurator()
    {
         return new DefaultComponentConfigurator();
    }

}
