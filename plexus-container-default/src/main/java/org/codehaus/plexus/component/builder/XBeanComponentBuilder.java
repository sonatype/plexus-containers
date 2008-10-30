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

import static org.apache.xbean.recipe.RecipeHelper.toClass;
import static org.codehaus.plexus.component.CastUtils.cast;

import org.apache.xbean.recipe.AbstractRecipe;
import org.apache.xbean.recipe.ConstructionException;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;
import org.apache.xbean.recipe.RecipeHelper;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.MapOrientedComponent;
import org.codehaus.plexus.component.collections.ComponentList;
import org.codehaus.plexus.component.collections.ComponentMap;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.composite.MapConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XBeanComponentBuilder implements ComponentBuilder {
    private ComponentManager componentManager;

    public XBeanComponentBuilder() {
    }

    public XBeanComponentBuilder(ComponentManager componentManager) {
        setComponentManager(componentManager);
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    protected MutablePlexusContainer getContainer() {
        return componentManager.getContainer();
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
        MutablePlexusContainer container = getContainer();
        if (realm == null) {
            realm = container.getComponentRealm(descriptor.getRealmId());
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(realm);
        try {
            ObjectRecipe recipe = createObjectRecipe(descriptor, realm);

            Object object;
            ComponentFactory componentFactory = container.getComponentFactoryManager().findComponentFactory(descriptor.getComponentFactory());
            if (JavaComponentFactory.class.equals(componentFactory.getClass())) {
                // xbean-reflect will create object and do injection
                object = recipe.create();
            } else {
                // todo figure out how to easily let xbean use the factory to construct the component
                // use object factory to construct component and then inject into that object
                object = componentFactory.newInstance(descriptor, realm, container);
                recipe.setProperties(object);
            }

            // todo figure out how to easily let xbean do this map oriented stuff (if it is actually used in plexus)
            if (object instanceof MapOrientedComponent) {
                MapOrientedComponent mapOrientedComponent = (MapOrientedComponent) object;
                processMapOrientedComponent(descriptor, mapOrientedComponent, realm);
            }

            return object;
        } catch (Exception e) {
            throw new ComponentLifecycleException("Error constructing component " + descriptor.getHumanReadableKey(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    public ObjectRecipe createObjectRecipe(ComponentDescriptor descriptor, ClassRealm realm) throws ComponentInstantiationException, PlexusConfigurationException {
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

        // MapOrientedComponents don't get normal injection
        if (!isMapOrientedClass(typeName, realm)) {
            for (ComponentRequirement requirement : cast(descriptor.getRequirements(), ComponentRequirement.class)) {
                String name = requirement.getFieldName();
                RequirementRecipe requirementRecipe = new RequirementRecipe(descriptor, requirement, getContainer(), name == null);

                if (name != null) {
                    recipe.setProperty(name, requirementRecipe);
                } else {
                    recipe.setAutoMatchProperty(requirement.getRole(), requirementRecipe);
                }
            }

            // add configuration data
            if (shouldConfigure(descriptor, realm)) {
                PlexusConfiguration configuration = descriptor.getConfiguration();
                if (configuration != null) {
                    for (String name : configuration.getAttributeNames()) {
                        String value;
                        try {
                            value = configuration.getAttribute(name);
                        } catch (PlexusConfigurationException e) {
                            throw new ComponentInstantiationException("Error getting value for attribute " + name, e);
                        }
                        name = fromXML(name);
                        recipe.setProperty(name, value);
                    }
                    for (PlexusConfiguration child : configuration.getChildren()) {
                        String name = child.getName();
                        name = fromXML(name);
                        if (child.getChildCount() == 0) {
                            recipe.setProperty(name, child.getValue());
                        } else {
                            recipe.setProperty(name, new PlexusConfigurationRecipe(child));
                        }
                    }
                }
            }
        }
        return recipe;
    }

    protected boolean shouldConfigure(ComponentDescriptor descriptor, ClassRealm realm) {
        String configuratorId = descriptor.getComponentConfigurator();

        if (StringUtils.isEmpty(configuratorId)) {
            return true;
        }

        try {
            ComponentConfigurator componentConfigurator = (ComponentConfigurator) getContainer().lookup(ComponentConfigurator.ROLE, configuratorId, realm);
            return componentConfigurator == null || componentConfigurator.getClass().equals(BasicComponentConfigurator.class);
        } catch (ComponentLookupException e) {
        }

        return true;
    }
    protected String fromXML(String elementName) {
        return StringUtils.lowercaseFirstLetter(StringUtils.removeAndHump(elementName, "-"));
    }

    protected void startComponentLifecycle(Object component, ClassRealm realm) throws ComponentLifecycleException {
        try {
            componentManager.getLifecycleHandler().start(component, componentManager, realm);
        } catch (PhaseExecutionException e) {
            throw new ComponentLifecycleException("Error starting component", e);
        }
    }

    public static class RequirementRecipe extends AbstractRecipe {
        private ComponentDescriptor componentDescriptor;
        private ComponentRequirement requirement;
        private PlexusContainer container;
        private boolean autoMatch;

        public RequirementRecipe(ComponentDescriptor componentDescriptor, ComponentRequirement requirement, PlexusContainer container, boolean autoMatch) {
            this.componentDescriptor = componentDescriptor;
            this.requirement = requirement;
            this.container = container;
            this.autoMatch = autoMatch;
        }

        public boolean canCreate(Type expectedType) {
            if (!autoMatch)
            {
                return true;
            }

            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            Class propertyType = toClass(expectedType);

            // Never auto match array, map or collection
            if (propertyType.isArray() || Map.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType) || requirement instanceof ComponentRequirementList) {
                return false;
            }

            // if the type to be created is an instance of the expceted type, return true
            try {
                ComponentDescriptor descriptor = ((MutablePlexusContainer) container).getComponentRepository().getComponentDescriptor(requirement.getRole(), requirement.getRoleHint(), realm);

                String typeName = descriptor.getImplementation();
                Class actualClass = realm.loadClass(typeName);

                if (RecipeHelper.isAssignable(expectedType, actualClass)) {
                    return true;
                }
            } catch (Exception e) {
            }

            return false;
        }

        @Override
        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            Class propertyType = toClass(expectedType);

            try {
                String role = requirement.getRole();
                List roleHints = null;
                if (requirement instanceof ComponentRequirementList) {
                    roleHints = ((ComponentRequirementList) requirement).getRoleHints();
                }

                Object assignment;
                if (propertyType.isArray()) {
                    assignment = new ArrayList<Object>(cast(container.lookupList(role, roleHints)));
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
                        assignment = container.lookupMap(role, roleHints);
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

        @Override
        public String toString() {
            return "RequirementRecipe[fieldName=" + requirement.getFieldName() + ", role=" + componentDescriptor.getRole() + "]";
        }
    }

    private class PlexusConfigurationRecipe extends AbstractRecipe {
        private final PlexusConfiguration child;

        public PlexusConfigurationRecipe(PlexusConfiguration child) {
            this.child = child;
        }

        public boolean canCreate(Type type) {
            try {
                ConverterLookup lookup = createConverterLookup();
                lookup.lookupConverterForType(toClass(type));
                return true;
            } catch (ComponentConfigurationException e) {
                return false;
            }
        }

        @Override
        protected Object internalCreate(Type expectedType, boolean lazyRefAllowed) throws ConstructionException {
            try {
                ConverterLookup lookup = createConverterLookup();
                ConfigurationConverter converter = lookup.lookupConverterForType(toClass(expectedType));

                // todo this will not work for static factories
                ObjectRecipe caller = (ObjectRecipe) RecipeHelper.getCaller();
                Class parentClass = toClass(caller.getType());

                Object value = converter.fromConfiguration(lookup, child, toClass(expectedType), parentClass, Thread.currentThread().getContextClassLoader(), new DefaultExpressionEvaluator());
                return value;
            } catch (ComponentConfigurationException e) {
                throw new ConstructionException("Unable to convert configuration for property " + child.getName() + " to " + toClass(expectedType).getName());
            }
        }

        private ConverterLookup createConverterLookup() {
            ClassRealm realm = (ClassRealm) Thread.currentThread().getContextClassLoader();
            ConverterLookup lookup = new DefaultConverterLookup();
            lookup.registerConverter( new ClassRealmConverter(realm) );
            return lookup;
        }
    }


    private boolean isMapOrientedClass(String typeName, ClassRealm realm) {
        try {
            // have to load the class to determine this, and that only works for components using the JavaComponentFactory
            Class clazz = realm.loadClass(typeName);
            return MapOrientedComponent.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void processMapOrientedComponent(ComponentDescriptor descriptor, MapOrientedComponent mapOrientedComponent, ClassRealm realm) throws ComponentConfigurationException, ComponentLookupException {
        MutablePlexusContainer container = getContainer();

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

        MapConverter converter = new MapConverter();
        ConverterLookup converterLookup = new DefaultConverterLookup();
        DefaultExpressionEvaluator expressionEvaluator = new DefaultExpressionEvaluator();
        PlexusConfiguration configuration = container.getConfigurationSource().getConfiguration( descriptor );

        if ( configuration != null )
        {
            Map context = (Map) converter.fromConfiguration(converterLookup,
                                                            configuration,
                                                            null,
                                                            null,
                                                            realm,
                                                            expressionEvaluator,
                                                            null );

            mapOrientedComponent.setComponentConfiguration( context );
        }
    }
}
