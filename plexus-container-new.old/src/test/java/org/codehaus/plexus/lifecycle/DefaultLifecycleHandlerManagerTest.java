package org.codehaus.plexus.lifecycle;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.ObjectBuilder;
import org.codehaus.plexus.lifecycle.avalon.AvalonLifecycleHandler;

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
            "  <default-lifecycle-handler>avalon</default-lifecycle-handler>" +
            "  <lifecycle-handlers>" +
            "    <lifecycle-handler implementation='org.codehaus.plexus.lifecycle.avalon.AvalonLifecycleHandler'>" +
            "      <id>avalon</id>" +
            "      <name>Avalon Lifecycle Handler</name>" +
            "      <begin-segment>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.LogEnablePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.ContextualizePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.ServicePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.ConfigurePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.InitializePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.StartPhase'/>" +
            "      </begin-segment>" +
            "      <suspend-segment>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.SuspendPhase'/>" +
            "      </suspend-segment>" +
            "      <resume-segment>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.ResumePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.RecontextualizePhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.ReconfigurePhase'/>" +
            "      </resume-segment>" +
            "      <end-segment>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.StopPhase'/>" +
            "        <phase implementation='org.codehaus.plexus.lifecycle.phase.DisposePhase'/>" +
            "      </end-segment>" +
            "    </lifecycle-handler>" +
            "  </lifecycle-handlers>" +
            "</lifecycle-handler-manager>";

        ObjectBuilder builder = new ObjectBuilder();

        builder.alias( "lifecycle-handler-manager", DefaultLifecycleHandlerManager.class );

        DefaultLifecycleHandlerManager lhm =
            (DefaultLifecycleHandlerManager) builder.build( new StringReader( configuration ), DefaultLifecycleHandlerManager.class );

        assertNotNull( lhm );

        assertEquals( "avalon", lhm.getDefaultLifecycleHandler() );

        AvalonLifecycleHandler lh = (AvalonLifecycleHandler) lhm.getLifecycleHandler( "avalon" );

        assertNotNull( lh );

        List beginSegment = lh.getBeginSegment();

        assertEquals( 6, beginSegment.size() );

        List suspendSegment = lh.getSuspendSegment();

        assertEquals( 1, suspendSegment.size() );

        List resumeSegment = lh.getResumeSegment();

        assertEquals( 3, resumeSegment.size() );

        List endSegment = lh.getEndSegment();

        assertEquals( 2, endSegment.size() );
    }
}
