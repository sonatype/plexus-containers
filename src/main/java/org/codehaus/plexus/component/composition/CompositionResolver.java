package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.util.List;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public interface CompositionResolver
{

    /**
     * @param componentDescriptor
     * @throws CompositionException when cycle is detected
     */
    void addComponentDescriptor( ComponentDescriptor componentDescriptor ) throws CompositionException;

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
