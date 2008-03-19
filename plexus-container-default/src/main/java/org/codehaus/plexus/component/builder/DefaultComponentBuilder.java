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
package org.codehaus.plexus.component.builder;

import java.lang.reflect.Method;

import org.codehaus.classworlds.ClassRealmAdapter;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.UndefinedComponentFactoryException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class DefaultComponentBuilder implements ComponentBuilder {
    private ComponentManager componentManager;

    public DefaultComponentBuilder() {
    }

    public DefaultComponentBuilder(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    public Object build(ComponentDescriptor descriptor, ClassRealm realm, ComponentBuildListener listener) throws ComponentInstantiationException, ComponentLifecycleException {
        if (listener != null) {
            listener.beforeComponentCreate(descriptor, realm);
        }

        Object component = createComponentInstance(descriptor, realm );

        if (listener != null) {
            listener.componentCreated(descriptor, component, realm);
        }

        startComponentLifecycle( component, realm );

        if (listener != null) {
            listener.componentConfigured(descriptor, component, realm);
        }

        return component;
    }

    protected Object createComponentInstance( ComponentDescriptor componentDescriptor,
                                              ClassRealm realm )
        throws ComponentInstantiationException, ComponentLifecycleException
    {
        String componentFactoryId = componentDescriptor.getComponentFactory();

        ComponentFactory componentFactory;

        Object component;

        try
        {

            MutablePlexusContainer container = componentManager.getContainer();
            componentFactory = container.getComponentFactoryManager().findComponentFactory( componentFactoryId );

            ClassRealm componentRealm;

            if ( realm == null )
            {
                componentRealm = container.getComponentRealm( componentDescriptor.getRealmId() );
            }
            else
            {
                componentRealm = realm;
            }

            try
            {
                component = componentFactory.newInstance( componentDescriptor, componentRealm, container );
            }
            catch ( AbstractMethodError e )
            {
                // ----------------------------------------------------------------------------
                // For compatibility with old ComponentFactories that use old ClassWorlds
                // ----------------------------------------------------------------------------

                org.codehaus.classworlds.ClassRealm cr = ClassRealmAdapter.getInstance( componentRealm );

                Method method;

                try
                {
                    method = componentFactory.getClass().getMethod( "newInstance", ComponentDescriptor.class, org.codehaus.classworlds.ClassRealm.class, PlexusContainer.class);

                    component = method.invoke( componentFactory, componentDescriptor, cr, container);
                }
                catch ( Exception mnfe )
                {
                    throw new ComponentInstantiationException(
                        "Unable to create component as factory '" + componentFactoryId + "' could not be found", e );
                }
            }
        }
        catch ( UndefinedComponentFactoryException e )
        {
            throw new ComponentInstantiationException(
                "Unable to create component as factory '" + componentFactoryId + "' could not be found", e );
        }

        return component;
    }

    protected void startComponentLifecycle( Object component, ClassRealm realm )
        throws ComponentLifecycleException
    {
        try
        {
            componentManager.getLifecycleHandler().start( component, componentManager, realm );
        }
        catch ( PhaseExecutionException e )
        {
            throw new ComponentLifecycleException( "Error starting component", e );
        }
    }

}
