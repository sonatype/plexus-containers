package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.component.manager.ComponentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractLifecycleHandler
    extends AbstractLogEnabled
    implements LifecycleHandler
{
    private Map entities;
    private List beginSegment;
    private List suspendSegment;
    private List resumeSegment;
    private List endSegment;
    private Configuration configuration;

    public AbstractLifecycleHandler()
    {
        beginSegment = new ArrayList();
        suspendSegment = new ArrayList();
        resumeSegment = new ArrayList();
        endSegment = new ArrayList();
    }

    public Map getEntities()
    {
        if ( entities == null )
        {
            entities = new HashMap();
        }

        return entities;
    }

    public void setEntities( Map entities )
    {
        this.entities = entities;
    }

    public void addEntity( String key, Object entity )
    {
        getEntities().put( key, entity );
    }

    // ----------------------------------------------------------------------
    // Begin Segment
    // ----------------------------------------------------------------------

    public void setBeginSegment( List beginSegment )
    {
        this.beginSegment = beginSegment;
    }

    public void addBeginSegmentPhase( Phase phase )
    {
        getBeginSegment().add( phase );
    }

    public List getBeginSegment()
    {
        return beginSegment;
    }

    // ----------------------------------------------------------------------
    // Suspend Segment
    // ----------------------------------------------------------------------

    public void setSuspendSegment( List suspendSegment )
    {
        this.suspendSegment = suspendSegment;
    }

    public void addSuspendSegmentPhase( Phase phase )
    {
        getSuspendSegment().add( phase );
    }

    public List getSuspendSegment()
    {
        return suspendSegment;
    }

    // ----------------------------------------------------------------------
    // Resume Segment
    // ----------------------------------------------------------------------

    public void setResumeSegment( List resumeSegment )
    {
        this.resumeSegment = resumeSegment;
    }

    public void addResumeSegmentPhase( Phase phase )
    {
        getResumeSegment().add( phase );
    }

    public List getResumeSegment()
    {
        return resumeSegment;
    }

    // ----------------------------------------------------------------------
    // End Segment
    // ----------------------------------------------------------------------

    public void setEndSegment( List endSegment )
    {
        this.endSegment = endSegment;
    }

    public void addEndSegmentPhase( Phase phase )
    {
        getEndSegment().add( phase );
    }

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
    public void startLifecycle( Object component, ComponentManager manager )
        throws Exception
    {
        for ( Iterator i = getBeginSegment().iterator(); i.hasNext(); )
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
    public void endLifecycle( Object component, ComponentManager manager )
        throws Exception
    {
        for ( Iterator i = getEndSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();
            phase.execute( component, manager );
        }
    }
}
