package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Map;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public interface ComponentComposerManager
{
    String ROLE = ComponentComposerManager.class.getName();


    void assembleComponent( Object component, final ComponentDescriptor componentDescriptor, final PlexusContainer container )
            throws CompositionException, UndefinedComponentComposerException, ComponentLookupException;

}