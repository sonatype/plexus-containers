package org.codehaus.plexus;

/*
 * Copyright 2001-2009 Codehaus Foundation.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

public class ClassRealmUtil
{

    public static Set<ClassRealm> getContextRealms( ClassWorld world )
    {
        Set<ClassRealm> realms = new LinkedHashSet<ClassRealm>();

        for ( ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader =
            classLoader.getParent() )
        {
            if ( classLoader instanceof ClassRealm )
            {
                realms.add( (ClassRealm) classLoader );

                Queue<ClassRealm> queue = new LinkedList<ClassRealm>();
                queue.add( (ClassRealm) classLoader );

                while ( !queue.isEmpty() )
                {
                    ClassRealm realm = queue.remove();

                    Collection<ClassRealm> importRealms = realm.getImportRealms();
                    for ( ClassRealm importRealm : importRealms )
                    {
                        if ( realms.add( importRealm ) )
                        {
                            queue.add( importRealm );
                        }
                    }

                    ClassRealm parentRealm = realm.getParentRealm();
                    if ( parentRealm != null && realms.add( parentRealm ) )
                    {
                        queue.add( parentRealm );
                    }
                }
            }
        }

        if ( world != null )
        {
            for ( Iterator<ClassRealm> it = realms.iterator(); it.hasNext(); )
            {
                ClassRealm realm = it.next();
                if ( realm.getWorld() != world )
                {
                    it.remove();
                }
            }
        }

        return realms;
    }

}
