package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractLifecycleHandler
    extends AbstractLogEnabled
    implements LifecycleHandler
{
    private String id = null;

    private String name = null;

    private Map entities;

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

    public Map getEntities()
    {
        if ( entities == null )
        {
            entities = new HashMap();
        }

        return entities;
    }

    public void addEntity( String key, Object entity )
    {
        getEntities().put( key, entity );
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

    /** Start a component's lifecycle.
     *
     *  @param component
     *
     *  @throws java.lang.Exception If an error occurs while attempting to beginSegment
     *          the component's lifecycle.
     */
    public void start( Object component, ComponentManager manager )
        throws Exception
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
        throws Exception
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
        throws Exception
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

    /** End a component's lifecycle.
     *
     *  @param component
     *
     *  @throws java.lang.Exception If an error occurs while attempting to endSegment
     *          the component's lifecycle.
     */
    public void end( Object component, ComponentManager manager )
        throws Exception
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
