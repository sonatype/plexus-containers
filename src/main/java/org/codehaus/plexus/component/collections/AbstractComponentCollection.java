package org.codehaus.plexus.component.collections;

/**
 * @author Jason van Zyl
 */

// We need to have the collection notified when a new implementation of a given role has
// been added to the container. We probably need some options so that we know when new
// component descriptors have been added to the system, and an option to keep the collection
// up-to-date when new implementations are added.

public class AbstractComponentCollection
{
    /**
     * The role of the components we are holding in this Map.
     */
    protected String role;

    public AbstractComponentCollection( String role )
    {
        this.role = role;
    }
}
