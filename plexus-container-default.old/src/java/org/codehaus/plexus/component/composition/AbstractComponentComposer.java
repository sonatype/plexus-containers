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

        field.setAccessible( true );

        try
        {
            field.set( target, component );
        }
        catch ( IllegalArgumentException e )
        {
            throw new CompositionException( e.getMessage() );
        }
        catch ( IllegalAccessException e )
        {
            throw new CompositionException( e.getMessage() );
        }
    }
}
