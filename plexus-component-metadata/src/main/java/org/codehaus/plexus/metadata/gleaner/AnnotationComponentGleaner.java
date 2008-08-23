/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.metadata.gleaner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * A class component gleaner which inspects each type for <tt>org.codehaus.plexus.component.annotations.*</tt> annotations
 * and when found translates them into a {@link ComponentDescriptor}.
 *
 * @version $Id$
 */
public class AnnotationComponentGleaner
    extends ComponentGleanerSupport
    implements ClassComponentGleaner
{
    public ComponentDescriptor glean(final Class clazz) throws ComponentGleanerException {
        assert clazz != null;

        // Cast to <?> so that we don't have to cast below
        Class<?> type = (Class<?>)clazz;

        // Skip abstract classes
        if (Modifier.isAbstract(type.getModifiers())) {
            return null;
        }
        
        Component anno = type.getAnnotation(Component.class);

        if (anno == null) {
            return null;
        }

        ComponentDescriptor component = new ComponentDescriptor();
        
        component.setRole(anno.role().getName());

        component.setRoleHint(filterEmptyAsNull(anno.hint()));

        component.setImplementation(type.getName());

        component.setVersion(filterEmptyAsNull(anno.version()));

        component.setComponentType(filterEmptyAsNull(anno.type()));

        component.setInstantiationStrategy(filterEmptyAsNull(anno.instantiationStrategy()));

        component.setLifecycleHandler(filterEmptyAsNull(anno.lifecycleHandler()));

        component.setComponentProfile(filterEmptyAsNull(anno.profile()));

        component.setComponentComposer(filterEmptyAsNull(anno.composer()));

        component.setComponentConfigurator(filterEmptyAsNull(anno.configurator()));

        component.setComponentFactory(filterEmptyAsNull(anno.factory()));

        component.setDescription(filterEmptyAsNull(anno.description()));

        component.setAlias(filterEmptyAsNull(anno.alias()));

        component.setIsolatedRealm(anno.isolatedRealm());

        for (Class t : getClasses(type)) {
            for (Field field : t.getDeclaredFields()) {
                ComponentRequirement requirement = findRequirement(field);

                if (requirement != null) {
                    component.addRequirement(requirement);
                }

                PlexusConfiguration config = findConfiguration(field);

                if (config != null) {
                    addChildConfiguration(component, config);
                }
            }

            //
            // TODO: Inspect methods?
            //
        }

        return component;
    }

    /**
     * Returns a list of all of the classes which the given type inherits from.
     */
    private List<Class> getClasses(Class<?> type) {
        assert type != null;

        List<Class> classes = new ArrayList<Class>();

        while (type != null) {
            classes.add(type);
            type = type.getSuperclass();

            //
            // TODO: See if we need to include interfaces here too?
            //
        }

        return classes;
    }

    private ComponentRequirement findRequirement(final Field field) {
        assert field != null;

        Requirement anno = field.getAnnotation(Requirement.class);

        if (anno == null) {
            return null;
        }

        Class<?> type = field.getType();

        ComponentRequirement requirement;

        if (isRequirementListType(type)) {
            requirement = new ComponentRequirementList();

            String[] hints = anno.hints();

            if (hints != null && hints.length > 0) {
                ((ComponentRequirementList)requirement).setRoleHints(Arrays.asList(hints));
            }

            //
            // TODO: See if we can glean any type details out of any generic information from the map or collection
            //
        }
        else {
            requirement = new ComponentRequirement();

            requirement.setRoleHint(filterEmptyAsNull(anno.hint()));
        }

        if (anno.role().isAssignableFrom(Object.class)) {
            requirement.setRole(type.getName());
        }
        else {
            requirement.setRole(anno.role().getName());
        }

        requirement.setFieldName(field.getName());

        requirement.setFieldMappingType(type.getName());

        return requirement;
    }

    private PlexusConfiguration findConfiguration(final Field field) {
        assert field != null;

        Configuration anno = field.getAnnotation(Configuration.class);

        if (anno == null) {
            return null;
        }

        String name = filterEmptyAsNull(anno.name());
        if (name == null) {
            name = field.getName();
        }
        name = deHump(name);
        
        XmlPlexusConfiguration config = new XmlPlexusConfiguration(name);

        String value = filterEmptyAsNull(anno.value());
        if (value != null) {
            config.setValue(value);
        }

        return config;
    }
}