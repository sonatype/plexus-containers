package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.PlexusContainer;

import java.lang.reflect.Field;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public abstract class AbstractComponentComposer
    implements ComponentComposer
{
    // parameterize the container and repository, i don't want to directly
    // couple their use. i just need to find descriptors and lookup
    // components.

    // instantiate component
    // compose
    // configure
    // the container and repository need to be in the lifecycle entities.

    // Maybe the repository should track the relationships between components ...

    public void assembleComponent( Object component,
                                   ComponentDescriptor componentDescriptor,
                                   PlexusContainer container,
                                   ComponentRepository componentRepository )
    {
        // The graph being used needs to support additions and removals at runtime.

        // We will probably need access to the container and the repository ... hmmm.

        // Need to recusviely walk through the descriptors building up the component.

        // How to keep track of components that have already been assembled.
    }

    /**
     * Assign a component to a target object by setting the appropriate field in
     * the target object. We find a match by looking at the component object's class
     * and match it up with the field of the same type in the target.
     *
     * @param component Component to assign to the target.
     * @param target Target component to which the component will be assigned.
     */
    protected void assignComponent( Object component, Object target )
        throws CompositionException
    {
        if ( target == null )
        {
            throw new CompositionException( "Target object is null." );
        }

        Class componentClass = component.getClass();

        Field[] fields = target.getClass().getDeclaredFields();

        Field field = null;

        for ( int i = 0; i < fields.length; i++ )
        {
            if ( componentClass.isAssignableFrom( fields[i].getType() ) )
            {
                field = fields[i];

                break;
            }
        }

        if ( field == null )
        {
            throw new CompositionException( "No field which is compatible in target object." );
        }

        field.setAccessible( true );

        try
        {
            field.set( target, component );
        }
        catch ( Exception e )
        {
            throw new CompositionException( "Error assigning field." );
        }
    }
}
