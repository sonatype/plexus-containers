package org.codehaus.plexus.component.repository;

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
import static org.codehaus.plexus.component.CastUtils.cast;
import org.codehaus.plexus.component.ComponentIndex;
import org.codehaus.plexus.component.composition.CompositionException;
import org.codehaus.plexus.component.composition.CompositionResolver;
import org.codehaus.plexus.component.composition.DefaultCompositionResolver;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;

import java.util.List;
import java.util.Map;

/** @author Jason van Zyl */
public class DefaultComponentRepository implements ComponentRepository
{
    private final ComponentIndex<ComponentDescriptor<?>> index = new ComponentIndex<ComponentDescriptor<?>>();

    private final CompositionResolver compositionResolver = new DefaultCompositionResolver();

    public <T> ComponentDescriptor<T> getComponentDescriptor( Class<T> type, String roleHint )
    {
        return (ComponentDescriptor<T>) index.get( type, roleHint );
    }

    public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( Class<T> type )
    {
        return cast( index.getAllAsMap( type ) );
    }

    public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( Class<T> type )
    {
        return cast( index.getAll( type ) );
    }

    public void removeComponentRealm( ClassRealm classRealm )
    {
        index.removeAll( classRealm );
    }

    public void addComponentDescriptor( ComponentDescriptor<?> componentDescriptor ) throws ComponentRepositoryException
    {
        synchronized ( index )
        {
            index.add( componentDescriptor.getRealm(),
                componentDescriptor.getImplementationClass(),
                componentDescriptor.getRoleHint(),
                componentDescriptor );

            try
            {
                compositionResolver.addComponentDescriptor( componentDescriptor );
            }
            catch ( CompositionException e )
            {
                index.remove( componentDescriptor );
                throw new ComponentRepositoryException( e.getMessage(), e );
            }
        }
    }
}
