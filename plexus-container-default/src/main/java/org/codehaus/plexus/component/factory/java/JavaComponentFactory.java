package org.codehaus.plexus.component.factory.java;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Component Factory for components written in Java Language which have default no parameter constructor
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class JavaComponentFactory
    extends AbstractComponentFactory
{
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        ClassRealm componentClassRealm = container.getComponentRealm( componentDescriptor.getComponentKey() );

        Class implementationClass = null;

        try
        {
            String implementation = componentDescriptor.getImplementation();

            //String role = componentDescriptor.getRole();

            //String roleHint = componentDescriptor.getRoleHint();

            //Class roleClass = classLoader.loadClass( role );

            //componentClassRealm.display();

            implementationClass = componentClassRealm.loadClass( implementation );

            //boolean implementationMatch = roleClass.isAssignableFrom( implementationClass );

            /*
            if ( !implementationMatch )
            {
                StringBuffer msg = new StringBuffer( "Instance of component " + componentDescriptor.getHumanReadableKey() );

                msg.append( " cannot be created. Role class: '" + role + "' " );

                msg.append( " is neither a superclass nor a superinterface of implementation class: ' " + implementation +"'" );

                throw new InstantiationException( msg.toString() );
            }
            */

            Object instance = implementationClass.newInstance();

            return instance;
        }
        catch ( InstantiationException e )
        {
            throw makeException( componentClassRealm, componentDescriptor, implementationClass, e );
        }
        catch ( ClassNotFoundException e )
        {
            throw makeException( componentClassRealm, componentDescriptor, implementationClass, e );
        }
        catch( IllegalAccessException e )
        {
            throw makeException( componentClassRealm, componentDescriptor, implementationClass, e );
        }
        catch( LinkageError e )
        {
            throw makeException( componentClassRealm, componentDescriptor, implementationClass, e );
        }
    }

    private ComponentInstantiationException makeException( ClassRealm componentClassRealm, ComponentDescriptor componentDescriptor, Class implementationClass, Throwable e )
    {
        // ----------------------------------------------------------------------
        // Display the realm when there is an error, We should probably return a string here so we
        // can incorporate this into the error message for easy debugging.
        // ----------------------------------------------------------------------
        componentClassRealm.display();

        String msg = "Could not instanciate component: " + componentDescriptor.getHumanReadableKey();

        return new ComponentInstantiationException( msg, e );
    }
}
