package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRepository;
import org.codehaus.plexus.PlexusContainer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Iterator;

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
        throws Exception
    {
        List requirements = componentRepository.getComponentDependencies( componentDescriptor );

        for ( Iterator i = requirements.iterator(); i.hasNext(); )
        {
            String role = (String) i.next();

            Object requirement = container.lookup( role );

            assignComponent( component, requirement );
        }
    }

    /**
     * Assign a requirement to a component object by setting the appropriate field in
     * the component object. We find a match by looking at the requirement object's class
     * and match it up with the field of the same type in the component.
     *
     * @param requirement Component to assign to the component.
     * @param component Target requirement to which the requirement will be assigned.
     */
    protected void assignComponent( Object component, Object requirement )
        throws CompositionException
    {
        if ( component == null )
        {
            throw new CompositionException( "Target object is null." );
        }

        Class componentClass = requirement.getClass();

        Field[] fields = component.getClass().getDeclaredFields();

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
            throw new CompositionException( "No field which is compatible in component object: " + field.getName() );
        }

        field.setAccessible( true );

        try
        {
            field.set( component, requirement );
        }
        catch ( Exception e )
        {
            throw new CompositionException( "Error assigning field." );
        }
    }
}
