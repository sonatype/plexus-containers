package org.codehaus.plexus.component.manager;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.xstream.XStreamTool;

import java.io.StringReader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultComponentManagerManagerTest
    extends TestCase
{
    public void testDefaultLifecycleHandlerManager()
        throws Exception
    {
        String configuration =
            "<component-manager-manager>" +
            "  <default-component-manager-id>singleton</default-component-manager-id>" +
            "  <component-managers>" +
            "    <component-manager implementation='org.codehaus.plexus.component.manager.PerLookupComponentManager'>" +
            "      <id>per-lookup</id>" +
            "      <description>New component instance per lookup which is disposed upon release</description>" +
            "    </component-manager>" +
            "    <component-manager implementation='org.codehaus.plexus.component.manager.PoolableComponentManager'>" +
            "      <id>poolable</id>" +
            "      <description>Component is pooled and only disposed when the pool gets too big. Growable pool</description>" +
            "      <initial-capacity>5</initial-capacity>" +
            "      <sweep-interval>5</sweep-interval>" +
            "      <max-capacity>30</max-capacity>" +
            "      <min-capacity>5</min-capacity>" +
            "      <trigger-size>20</trigger-size>" +
            "    </component-manager>" +
            "    <component-manager implementation='org.codehaus.plexus.component.manager.ClassicSingletonComponentManager'>" +
            "      <id>singleton</id>" +
            "      <description>The classic singleton manager. Once no more compenents are connected the" +
            "      instance is disposed</description>" +
            "    </component-manager>" +
            "    <component-manager implementation='org.codehaus.plexus.component.manager.KeepAliveSingletonComponentManager'>" +
            "      <id>singleton-keep-alive</id>" +
            "      <description>Singleton instance manager which keeps a single instance alive until" +
            "      the component repository is disposed.</description>" +
            "    </component-manager>" +
            "  </component-managers>" +
            "</component-manager-manager>";

        XStreamTool builder = new XStreamTool();

        builder.alias( "component-manager-manager", DefaultComponentManagerManager.class );

        DefaultComponentManagerManager cmm =
            (DefaultComponentManagerManager) builder.build( new StringReader( configuration ), DefaultComponentManagerManager.class );

        assertNotNull( cmm );

        assertEquals( "singleton", cmm.getDefaultComponentManager().getId() );

        ComponentManager componentManager = cmm.getComponentManager( "singleton" );

        assertNotNull( componentManager );

        componentManager = cmm.getComponentManager( "singleton-keep-alive" );

        assertNotNull( componentManager );

        componentManager = cmm.getComponentManager( "poolable" );

        assertNotNull( componentManager );

        componentManager = cmm.getComponentManager( "per-lookup" );

        assertNotNull( componentManager );

        try
        {
            cmm.getComponentManager( "non-existent-id" );

            fail( "UndefinedLifecycleHandlerException should be thrown." );
        }
        catch ( UndefinedComponentManagerException e )
        {
            // do nothing.
        }
    }
}
