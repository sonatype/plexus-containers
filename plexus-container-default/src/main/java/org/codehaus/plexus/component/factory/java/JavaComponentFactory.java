package org.codehaus.plexus.component.factory.java;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.lang.reflect.Modifier;

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
    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        Class implementationClass = null;

        try
        {
            String implementation = componentDescriptor.getImplementation();

            implementationClass = classRealm.loadClass( implementation );

            int modifiers = implementationClass.getModifiers();

            if ( Modifier.isInterface( modifiers ) )
            {
                throw new ComponentInstantiationException(
                    "Cannot instantiate implementation '" + implementation + "' because the class is a interface." );
            }

            if ( Modifier.isAbstract( modifiers ) )
            {
                throw new ComponentInstantiationException(
                    "Cannot instantiate implementation '" + implementation + "' because the class is abstract." );
            }

            Object instance = implementationClass.newInstance();

            return instance;
        }
        catch ( InstantiationException e )
        {
            //PLXAPI: most probably cause of this is the implementation class not having
            //        a default constructor.
            throw makeException( classRealm, componentDescriptor, implementationClass, e );
        }
        catch ( ClassNotFoundException e )
        {
            classRealm.display();
            throw makeException( classRealm, componentDescriptor, implementationClass, e );
        }
        catch ( IllegalAccessException e )
        {
            throw makeException( classRealm, componentDescriptor, implementationClass, e );
        }
        catch ( LinkageError e )
        {
            throw makeException( classRealm, componentDescriptor, implementationClass, e );
        }
    }

    private ComponentInstantiationException makeException( ClassRealm componentClassRealm,
                                                           ComponentDescriptor componentDescriptor,
                                                           Class implementationClass,
                                                           Throwable e )
    {
        // ----------------------------------------------------------------------
        // Display the realm when there is an error, We should probably return a string here so we
        // can incorporate this into the error message for easy debugging.
        // ----------------------------------------------------------------------

        String msg;

        if ( componentClassRealm == null )
        {
            msg = "classRealm is null for " + componentDescriptor;
        }
        else
        {
            msg = "Could not instantiate component: " + componentDescriptor.getHumanReadableKey() + " realm: "
                + componentClassRealm.getId();
        }

        return new ComponentInstantiationException( msg, e );
    }
}
