package org.codehaus.plexus.lifecycle;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
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
     * @deprecated
     */
    public void start( Object component, ComponentManager manager )
        throws PhaseExecutionException
    {
        start( component, manager, manager.getContainer().getLookupRealm( component ) );
    }

    /**
     * Start a component's lifecycle.
     */
    public void start( Object component, ComponentManager manager, ClassRealm realm  )
        throws PhaseExecutionException
    {
        if ( segmentIsEmpty( getBeginSegment() ) )
        {
            return;
        }

        for ( Iterator i = getBeginSegment().iterator(); i.hasNext(); )
        {
            Phase phase = (Phase) i.next();

            phase.execute( component, manager, realm );
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

            phase.execute( component, manager, manager.getContainer().getLookupRealm( component ) );
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

            phase.execute( component, manager, manager.getContainer().getLookupRealm( component ) );
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

            phase.execute( component, manager, manager.getContainer().getLookupRealm( component ) );
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
