package org.codehaus.plexus.lifecycle;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.xstream.XStreamTool;
import org.codehaus.plexus.personality.avalon.AvalonLifecycleHandler;

import java.io.StringReader;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultLifecycleHandlerManagerTest
    extends TestCase
{
    public void testDefaultLifecycleHandlerManager()
        throws Exception
    {
        String configuration =
            "<lifecycle-handler-manager implementation='org.codehaus.plexus.lifecycle.DefaultLifecycleHandlerManager'>" +
            "  <default-lifecycle-handler-id>avalon</default-lifecycle-handler-id>" +
            "  <lifecycle-handlers>" +
            "    <lifecycle-handler implementation='org.codehaus.plexus.personality.avalon.AvalonLifecycleHandler'>" +
            "      <id>avalon</id>" +
            "      <name>Avalon Lifecycle Handler</name>" +
            "      <begin-segment>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.LogEnablePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.ContextualizePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.ServicePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.ConfigurePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.InitializePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.StartPhase'/>" +
            "      </begin-segment>" +
            "      <suspend-segment>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.SuspendPhase'/>" +
            "      </suspend-segment>" +
            "      <resume-segment>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.ResumePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.RecontextualizePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.ReconfigurePhase'/>" +
            "      </resume-segment>" +
            "      <end-segment>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.StopPhase'/>" +
            "        <phase implementation='org.codehaus.plexus.personality.avalon.lifecycle.phase.DisposePhase'/>" +
            "      </end-segment>" +
            "    </lifecycle-handler>" +
            "  </lifecycle-handlers>" +
            "</lifecycle-handler-manager>";

        XStreamTool builder = new XStreamTool();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        DefaultLifecycleHandlerManager lhm =
            (DefaultLifecycleHandlerManager) builder.build( new StringReader( configuration ), DefaultLifecycleHandlerManager.class );

        assertNotNull( lhm );

        assertEquals( "avalon", lhm.getDefaultLifecycleHandler().getId() );

        AvalonLifecycleHandler lh = (AvalonLifecycleHandler) lhm.getLifecycleHandler( "avalon" );

        assertNotNull( lh );

        assertEquals( "Avalon Lifecycle Handler", lh.getName() );

        List beginSegment = lh.getBeginSegment();

        assertEquals( 6, beginSegment.size() );

        List suspendSegment = lh.getSuspendSegment();

        assertEquals( 1, suspendSegment.size() );

        List resumeSegment = lh.getResumeSegment();

        assertEquals( 3, resumeSegment.size() );

        List endSegment = lh.getEndSegment();

        assertEquals( 2, endSegment.size() );

        // Default lifecycle handler

        LifecycleHandler defaultLifecycleHandler = lhm.getDefaultLifecycleHandler();

        assertNotNull( defaultLifecycleHandler );

        try
        {
            lhm.getLifecycleHandler( "non-existent-id" );

            fail( "UndefinedLifecycleHandlerException should be thrown." );
        }
        catch( UndefinedLifecycleHandlerException e )
        {
            // do nothing.
        }
    }
}
