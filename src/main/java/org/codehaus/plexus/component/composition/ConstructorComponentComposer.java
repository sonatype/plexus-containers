package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.PlexusContainer;

import java.util.Map;

/**
 * note:jvz This really indicates there is a flaw in the design of the component composer as constructors will not work. If the
 * component has no default constructor then the component must be created with using the constructor that has requirements
 * as its parameters. So in this case the factory is the means of composition as well. Really you need to collect all the
 * information and create and compose.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ConstructorComponentComposer
    extends AbstractComponentComposer
{
    public void assignRequirement( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   ComponentRequirement requirement,
                                   PlexusContainer container,
                                   Map compositionContext )
        throws CompositionException
    {
    }
}



