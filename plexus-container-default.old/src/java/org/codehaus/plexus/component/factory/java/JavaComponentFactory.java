package org.codehaus.plexus.component.factory.java;

import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.NoSuchRealmException;

import java.util.Iterator;

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
        ClassRealm componentClassRealm;

        try
        {
            componentClassRealm = classRealm.getWorld().getRealm( componentDescriptor.getComponentKey() );
        }
        catch ( NoSuchRealmException e )
        {
            componentClassRealm = classRealm;
        }

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
            String msg = "Component " + componentDescriptor.getHumanReadableKey() + " cannot be instantiated: " + e.getMessage();

            throw new ComponentInstantiationException( msg, e );
        }
    }
}
