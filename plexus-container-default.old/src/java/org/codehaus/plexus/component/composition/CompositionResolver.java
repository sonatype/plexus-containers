package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 *
 * @version $Id$
 */
public interface CompositionResolver
{
 
    void addComponentDescriptor( ComponentDescriptor componentDescriptor );
    
    /**
     * Returns the list of names of components which are required  
     * by the component of given componentKey.
     * 
     * @param componentKey The name of the component
     * @return The list of components which are required by given component 
     */
    List getRequirements( String componentKey );
    
    
    /**
     * Returns the list of names of components which are using the component.
     * of given componentKey
     * 
     * @param componentKey The name of the component
     * @return The list of components which are requiring given component 
     */
    List findRequirements( String componentKey );
    
    
}
