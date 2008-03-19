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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xbean.recipe.AbstractRecipe;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.RecipeHelper;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import static org.codehaus.plexus.component.CastUtils.cast;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import org.codehaus.plexus.util.StringUtils;

public class XBeanComponentBuilder implements ComponentBuilder {
    private ComponentManager componentManager;

    public XBeanComponentBuilder() {
    }

    public XBeanComponentBuilder(ComponentManager componentManager) {
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

        Object component = createComponentInstance(descriptor, realm);

        if (listener != null) {
            listener.componentCreated(descriptor, component, realm);
        }

        startComponentLifecycle(component, realm);

        if (listener != null) {
            listener.componentConfigured(descriptor, component, realm);
        }

        return component;
    }

    protected Object createComponentInstance(ComponentDescriptor descriptor, ClassRealm realm) throws ComponentInstantiationException, ComponentLifecycleException {

        if (realm == null) {
            realm = componentManager.getContainer().getComponentRealm(descriptor.getRealmId());
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(realm);
        try {
            String typeName = descriptor.getImplementation();
            String factoryMethod = null;
            String[] constructorArgNames = null;
            Class[] constructorArgTypes = null;

            ObjectRecipe recipe = new ObjectRecipe(typeName,
                    factoryMethod,
                    constructorArgNames,
                    constructorArgTypes);
            recipe.allow(Option.FIELD_INJECTION);
            recipe.allow(Option.PRIVATE_PROPERTIES);

            // MapOrientedComponent don't get normal requirement injection
            if (isMapOrientedClass(typeName, realm)) {
                for (ComponentRequirement requirement : cast(descriptor.getRequirements(), ComponentRequirement.class)) {
                    String name = requirement.getFieldName();
                    // todo why are field names null?
                    if (name != null) {
                        RequirementRecipe requirementRecipe = new RequirementRecipe(descriptor, requirement, componentManager.getContainer());

                        recipe.setProperty(name, requirementRecipe);
                    }
                }
            }

            // add configuration data
            PlexusConfiguration configuration = descriptor.getConfiguration();
            if (configuration != null) {
                for (String name : configuration.getAttributeNames()) {
                    String value;
                    try {
                        value = configuration.getAttribute(name);
                    } catch (PlexusConfigurationException e) {
                        throw new ComponentInstantiationException("Error getting value for attribute " + name, e);
                    }
                    recipe.setProperty(name, value);
                }
//                for (PlexusConfiguration child : configuration.getChildren()) {
//                    // todo wrap with xstream recipe
//                }
            }

            // JavaComponentFactory
            Object object;
            ComponentFactory componentFactory = componentManager.getContainer().getComponentFactoryManager().findComponentFactory(descriptor.getComponentFactory());
            if (JavaComponentFactory.class.equals(componentFactory.getClass())) {
                // xbean-reflect will create object and do injection
                object = recipe.create();
            } else {
                // todo figure out how to easily let xbean use the factory to construct the component
                // use object factory to construct component and then inject into that object
                object = componentFactory.newInstance(descriptor, realm, componentManager.getContainer());
                recipe.setProperties(object);
            }

            // todo figure out how to easily let xbean do this map oriented stuff (if it is actually used in plexus)
            if (object instanceof MapOrientedComponent) {
                MapOrientedComponent mapOrientedComponent = (MapOrientedComponent) object;
                addComponentRequirements(descriptor, mapOrientedComponent);
            }

            return object;
        } catch (Exception e) {
            throw new ComponentLifecycleException("Error constructing component " + descriptor.getHumanReadableKey(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    protected void startComponentLifecycle(Object component, ClassRealm realm) throws ComponentLifecycleException {
        try {
            componentManager.getLifecycleHandler().start(component, componentManager, realm);
        }
        catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error starting component", e);
        }
    }

    public static class RequirementRecipe extends AbstractRecipe {
        private ComponentDescriptor componentDescriptor;
        private ComponentRequirement requirement;
        private PlexusContainer container;

        public RequirementRecipe(ComponentDescriptor componentDescriptor, ComponentRequirement requirement, PlexusContainer container) {
            this.componentDescriptor = componentDescriptor;
            this.requirement = requirement;
            this.container = container;
        }

        public boolean canCreate(Type type) {
            return true;
        }

        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            Class propertyType = RecipeHelper.toClass(expectedType);

            try {
                String role = requirement.getRole();
                List roleHints = null;
                if (requirement instanceof ComponentRequirementList) {
                    roleHints = ((ComponentRequirementList) requirement).getRoleHints();
                }

                Object assignment;
                if (propertyType.isArray()) {
                    assignment = new ArrayList<Object>(cast(container.lookupList(role, roleHints, realm)));
                }

                // Map.class.isAssignableFrom( clazz ) doesn't make sense, since Map.class doesn't really
                // have a meaningful superclass.
                else {
                    if (Map.class.equals(propertyType)) {
                        // todo this is a lazy map
                        assignment = new ComponentMap(container,
                                realm,
                                role,
                                roleHints,
                                componentDescriptor.getHumanReadableKey());
                    }
                    // List.class.isAssignableFrom( clazz ) doesn't make sense, since List.class doesn't really
                    // have a meaningful superclass other than Collection.class, which we'll handle next.
                    else if (List.class.equals(propertyType)) {
                        // todo this is a lazy list
                        assignment = new ComponentList(container,
                                realm,
                                role,
                                roleHints,
                                componentDescriptor.getHumanReadableKey());
                    }
                    // Set.class.isAssignableFrom( clazz ) doesn't make sense, since Set.class doesn't really
                    // have a meaningful superclass other than Collection.class, and that would make this
                    // if-else cascade unpredictable (both List and Set extend Collection, so we'll put another
                    // check in for Collection.class.
                    else if (Set.class.equals(propertyType) || Collection.class.isAssignableFrom(propertyType)) {
                        // todo why isn't this lazy as above?
                        assignment = container.lookupMap(role, roleHints, realm);
                    } else if (Logger.class.equals(propertyType)) {
                        // todo magic reference
                        assignment = container.getLoggerManager().getLoggerForComponent(componentDescriptor.getRole());
                    } else if (PlexusContainer.class.equals(propertyType)) {
                        // todo magic reference
                        assignment = container;
                    } else {
                        String roleHint = requirement.getRoleHint();
                        assignment = container.lookup(role, roleHint, realm);
                    }
                }

                return assignment;
            } catch (ComponentLookupException e) {
                throw new ConstructionException("Composition failed of field " + requirement.getFieldName() + " "
                        + "in object of type " + componentDescriptor.getImplementation() + " because the requirement "
                        + requirement + " was missing (lookup realm: " + realm.getId() + ")", e);
            }
        }
    }

    private boolean isMapOrientedClass(String typeName, ClassRealm realm) {
        try {
            // have to load the class to determine this, and that only works for components using the JavaComponentFactory
            return !MapOrientedComponent.class.isAssignableFrom(realm.loadClass(typeName));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void addComponentRequirements(ComponentDescriptor descriptor, MapOrientedComponent mapOrientedComponent) throws ComponentConfigurationException, ComponentLookupException {
        MutablePlexusContainer container = componentManager.getContainer();

        for (ComponentRequirement requirement : cast(descriptor.getRequirements(), ComponentRequirement.class)) {
            String role = requirement.getRole();
            String hint = requirement.getRoleHint();
            String mappingType = requirement.getFieldMappingType();

            Object value;

            // if the hint is not empty (and not default), we don't care about mapping type...
            // it's a single-value, not a collection.
            if (StringUtils.isNotEmpty(hint) && !hint.equals(PlexusConstants.PLEXUS_DEFAULT_HINT)) {
                value = container.lookup(role, hint);
            } else if ("single".equals(mappingType)) {
                value = container.lookup(role, hint);
            } else if ("map".equals(mappingType)) {
                value = container.lookupMap(role);
            } else if ("set".equals(mappingType)) {
                value = new HashSet<Object>(cast(container.lookupList(role)));
            } else {
                value = container.lookup(role, hint);
            }

            mapOrientedComponent.addComponentRequirement(requirement, value);
        }
    }
}
