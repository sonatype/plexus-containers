package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractLifecycleHandler
    implements LifecycleHandler
{
    private String id = null;

    private String name = null;

    private List beginSegment;

    private List suspendSegment;

    private List resumeSegment;

    private List endSegment;

    public AbstractLifecycleHandler()
    {
        beginSegment = new ArrayList();

        suspendSegment = new ArrayList();

        resumeSegment = new ArrayList();

        endSegment = new ArrayList();
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    // Begin Segment
    // ----------------------------------------------------------------------

    public List getBeginSegment()
    {
        return beginSegment;
    }

    // ----------------------------------------------------------------------
    // Suspend Segment
    // ----------------------------------------------------------------------

    public List getSuspendSegment()
    {
        return suspendSegment;
    }

    // ----------------------------------------------------------------------
    // Resume Segment
    // ----------------------------------------------------------------------

    public List getResumeSegment()
    {
        return resumeSegment;
    }

    // ----------------------------------------------------------------------
    // End Segment
    // ----------------------------------------------------------------------

    public List getEndSegment()
    {
        return endSegment;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /**
     * Start a component's lifecycle.
     */
    public void start( Object component, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( segmentIsEmpty( getBeginSegment() ) )
        {
            return;
        }

        for ( Iterator i = getBeginSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();

            phase.execute( component, manager );
        }
    }

    public void suspend( Object component, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( segmentIsEmpty( getSuspendSegment() ) )
        {
            return;
        }

        for ( Iterator i = getSuspendSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();

            phase.execute( component, manager );
        }
    }

    public void resume( Object component, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( segmentIsEmpty( getResumeSegment() ) )
        {
            return;
        }

        for ( Iterator i = getResumeSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();

            phase.execute( component, manager );
        }
    }

    /**
     * End a component's lifecycle.
     */
    public void end( Object component, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( segmentIsEmpty( getEndSegment() ) )
        {
            return;
        }

        for ( Iterator i = getEndSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();

            phase.execute( component, manager );
        }
    }

    private boolean segmentIsEmpty( List segment )
    {
        if ( segment == null || segment.size() == 0 )
        {
            return true;
        }

        return false;
    }
}
