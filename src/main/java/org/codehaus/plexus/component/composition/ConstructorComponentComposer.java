package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.PlexusContainer;

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
    implements ComponentComposer
{
    public String getId()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container )
        throws CompositionException, UndefinedComponentComposerException
    {
    }
}
