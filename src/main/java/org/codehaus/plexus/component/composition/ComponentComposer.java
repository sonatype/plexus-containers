package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Revision$
 * @todo michal:  I think that ideally component composer should somehow participate in parsing of
 * requiremnts section of configuration file
 * as this section tells how to do the composition
 */
public interface ComponentComposer
{
    static String ROLE = ComponentComposer.class.getName();

    String getId();

    /**
     * @param component
     * @param componentDescriptor
     * @param container
     *
     * @return List of ComponentDescriptors which were used by ComponentComposer

     * @throws CompositionException
     * @throws UndefinedComponentComposerException
     *
     */
    public List assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
            throws CompositionException, UndefinedComponentComposerException;
}
