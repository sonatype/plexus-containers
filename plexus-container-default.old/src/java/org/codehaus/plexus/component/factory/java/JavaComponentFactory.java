package org.codehaus.plexus.component.factory.java;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.NoSuchRealmException;
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
    /**
     * @todo which exception shold be thrown if '!implementationMatch'?
     */
    public Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        ClassRealm componentClassRealm = container.getComponentRealm( componentDescriptor.getComponentKey() );

        try
        {
            String implementation = componentDescriptor.getImplementation();

            //String role = componentDescriptor.getRole();

            //String roleHint = componentDescriptor.getRoleHint();

            //Class roleClass = classLoader.loadClass( role );

            //componentClassRealm.display();

            Class implementationClass = componentClassRealm.loadClass( implementation );

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

            //componentClassRealm.display();

            Object instance = implementationClass.newInstance();

            return instance;
        }
        catch ( Exception e )
        {
            componentClassRealm.display();
            // Display the realm when there is an error, We should probably return a string here so we
            // can incorporate this into the error message for easy debugging.

            String msg = "Component " + componentDescriptor.getHumanReadableKey() + " cannot be instantiated: " + e.getMessage();

            throw new ComponentInstantiationException( msg, e );
        }
    }
}
