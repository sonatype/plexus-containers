package org.codehaus.plexus.component.configurator;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.component.repository.ComponentDescriptor;


public class BasicComponentConfiguratorTest extends AbstractComponentConfiguratorTest
{
    public BasicComponentConfiguratorTest( String s )
    {
        super( s );
    }

    protected ComponentConfigurator getComponentConfigurator()
    {
         return new BasicComponentConfigurator();
    }

}
