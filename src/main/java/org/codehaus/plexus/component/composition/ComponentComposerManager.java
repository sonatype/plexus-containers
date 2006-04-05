package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * @author Jason van Zyl
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public interface ComponentComposerManager
{
    String ROLE = ComponentComposerManager.class.getName();

    void assembleComponent( Object component, ComponentDescriptor componentDescriptor, PlexusContainer container )
        throws CompositionException, UndefinedComponentComposerException;
}
