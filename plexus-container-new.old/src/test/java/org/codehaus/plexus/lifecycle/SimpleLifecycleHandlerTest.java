package org.codehaus.plexus.lifecycle;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.codehaus.plexus.lifecycle.phase.MoPhase;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.component.manager.ComponentManager;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class SimpleLifecycleHandlerTest
    extends TestCase
{
    public void testSimpleLifecycleHandler()
        throws Exception
    {
        SimpleLifecycleHandler h = new SimpleLifecycleHandler();

        Map entities = new HashMap();

        Map context = new HashMap();

        entities.put( "context", context );

        h.setEntities( entities );

        String name = "jason";

        h.addEntity( "name", name );

        Map retrievedEntities = h.getEntities();

        assertEquals( context, retrievedEntities.get( "context" ) );

        assertEquals( name, retrievedEntities.get( "name" ) );

        // Begin Segment

        List beginSegment = new ArrayList();

        h.setBeginSegment( beginSegment );

        h.addBeginSegmentPhase( new SimplePhase() );

        assertEquals( beginSegment, h.getBeginSegment() );

        // Suspend Segment

        List suspendSegment = new ArrayList();

        h.setSuspendSegment( suspendSegment );

        h.addSuspendSegmentPhase( new SimplePhase() );

        assertEquals( suspendSegment, h.getSuspendSegment() );

        // Resume Segment

        List resumeSegment = new ArrayList();

        h.setResumeSegment( resumeSegment );

        h.addResumeSegmentPhase( new SimplePhase() );

        assertEquals( resumeSegment, h.getResumeSegment() );

        // End Segment

        List endSegment = new ArrayList();

        h.setEndSegment( endSegment );

        h.addEndSegmentPhase( new SimplePhase() );

        assertEquals( endSegment, h.getEndSegment() );
    }

    class SimplePhase
        extends AbstractPhase
    {
        public void execute( Object component, ComponentManager cm )
        {
        }
    }
}
