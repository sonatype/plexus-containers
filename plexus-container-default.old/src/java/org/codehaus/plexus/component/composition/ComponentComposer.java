package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.PlexusContainer;

public interface ComponentComposer
{
    static String ROLE = ComponentComposer.class.getName();

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container,
                                   ComponentRepository componentRepository );
}
