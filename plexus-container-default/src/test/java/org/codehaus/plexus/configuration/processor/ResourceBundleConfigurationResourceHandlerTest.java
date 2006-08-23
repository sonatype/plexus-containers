package org.codehaus.plexus.configuration.processor;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:andy@handyande.co.uk">Andrew Williams</a>
 * @version $Id: PropertyConfigurationResourceHandlerTest.java 1747 2005-04-19 05:38:54Z brett $
 */
public class ResourceBundleConfigurationResourceHandlerTest
    extends TestCase
{
    public void testPropertyConfigurationResourceHandler()
        throws Exception
    {
        ResourceBundleConfigurationResourceHandler h = new ResourceBundleConfigurationResourceHandler();

        Map parameters = new HashMap();

        parameters.put( "source", "inline-configuration" );

        PlexusConfiguration[] processed = h.handleRequest( parameters );

        PlexusConfiguration p = processed[0];

        assertEquals( "andrew", p.getChild( "first-name" ).getValue() );

        assertEquals( "williams", p.getChild( "last-name" ).getValue() );
    }
}
