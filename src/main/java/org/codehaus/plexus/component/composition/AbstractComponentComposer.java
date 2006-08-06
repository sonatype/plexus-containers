package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo just pass around a containerContext {component,descriptor,container}
 * @todo cleanup error messaging, pull out of autowire composer and generalize
 */
public abstract class AbstractComponentComposer
    extends AbstractLogEnabled
    implements ComponentComposer
{
    private String id;

    // ----------------------------------------------------------------------
    // Composition Life Cycle
    // ----------------------------------------------------------------------

    public void verifyComponentSuitability( Object component )
        throws CompositionException
    {
    }

    public Map createCompositionContext( Object component, ComponentDescriptor descriptor )
        throws CompositionException
    {
        return Collections.EMPTY_MAP;
    }

    public List gleanAutowiringRequirements( Map compositionContext, PlexusContainer container )
        throws CompositionException
    {
        return Collections.EMPTY_LIST;
    }

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
        throws CompositionException
    {
        // ----------------------------------------------------------------------
        // If a ComponentComposer wishes to check any criteria before attempting
        // composition it may do so here.
        // ----------------------------------------------------------------------

        verifyComponentSuitability( component );

        Map compositionContext = createCompositionContext( component, componentDescriptor );

        // ----------------------------------------------------------------------
        // If the ComponentDescriptor is null then we are attempting to autowire
        // this component which means that Plexus has just been handled a
        // POJO to wire up.
        // ----------------------------------------------------------------------

        List requirements;

        if ( componentDescriptor == null )
        {
            // Create a componentDescriptor to keep everything happy when we're trying to autowire.

            componentDescriptor = new ComponentDescriptor();

            componentDescriptor.setImplementation( component.getClass().getName() );

            componentDescriptor.setRole( component.getClass().getName() );

            requirements = gleanAutowiringRequirements( compositionContext, container );

            componentDescriptor.addRequirements( requirements );

            try
            {
                container.addComponentDescriptor( componentDescriptor );
            }
            catch ( ComponentRepositoryException e )
            {
                // this should never happen, we never took into account creating component
                // descriptors on the fly by gleaning information.
            }
        }
        else
        {
            requirements = componentDescriptor.getRequirements();
        }

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) i.next();

            assignRequirement( component, componentDescriptor, requirement, container, compositionContext );
        }
    }

    public String getId()
    {
        return id;
    }
}
