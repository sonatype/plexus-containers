package org.codehaus.plexus.component.composition;

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
