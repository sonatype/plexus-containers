package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;

import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Revision$
 */
public interface ComponentComposer
{
    static String ROLE = ComponentComposer.class.getName();

    String getId();

    void assembleComponent( Object component,
                            ComponentDescriptor componentDescriptor,
                            PlexusContainer container )
        throws CompositionException;

    void verifyComponentSuitability( Object component )
        throws CompositionException;

    Map createCompositionContext( Object component, ComponentDescriptor descriptor )
        throws CompositionException;

    List gleanAutowiringRequirements( Map compositionContext, PlexusContainer container )
        throws CompositionException;

    void assignRequirement( Object component,
                            ComponentDescriptor componentDescriptor,
                            ComponentRequirement componentRequirement,
                            PlexusContainer container, Map compositionContext )
        throws CompositionException;
}
