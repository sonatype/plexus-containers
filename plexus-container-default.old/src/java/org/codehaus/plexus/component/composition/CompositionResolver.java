package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface CompositionResolver
{
 
    void addComponentDescriptor( ComponentDescriptor componentDescriptor );
    List getComponentDependencies( String componentKey );
    
}
