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

import java.io.IOException;
import java.io.InputStream;
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
import org.codehaus.plexus.metadata.ann.AnnClass;
import org.codehaus.plexus.metadata.ann.AnnField;
import org.codehaus.plexus.metadata.ann.AnnReader;
import org.codehaus.plexus.util.IOUtil;

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
    public ComponentDescriptor glean(String className, ClassLoader cl) throws ComponentGleanerException 
    {
        assert className != null;
        assert cl != null;

        AnnClass annClass = readClass(className.replace('.', '/'), cl);
        
        // Skip abstract classes
        if (Modifier.isAbstract(annClass.getAccess())) {
            return null;
        }
        
        Component anno = annClass.getAnnotation(Component.class);

        if (anno == null) {
            return null;
        }

        ComponentDescriptor component = new ComponentDescriptor();
        
        component.setRole(anno.role().getName());

        component.setRoleHint(filterEmptyAsNull(anno.hint()));

        component.setImplementation(className);

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

        for (AnnClass c : getClasses(annClass, cl)) {
            for (AnnField field : c.getFields().values()) {
                ComponentRequirement requirement = findRequirement(field, c, cl);

                if (requirement != null) {
                    component.addRequirement(requirement);
                }

                PlexusConfiguration config = findConfiguration(field, c, cl);

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

    private AnnClass readClass(String className, ClassLoader cl) throws ComponentGleanerException 
    {
        InputStream is = null;
        try 
        {
          is = cl.getResourceAsStream(className + ".class");
          return AnnReader.read(is, cl);
        } 
        catch (IOException ex) 
        {
          throw new ComponentGleanerException("Can't read class " + className, ex);
        }
        finally
        {
          IOUtil.close(is);
        }
    }

    /**
     * Returns a list of all of the classes which the given type inherits from.
     */
    private List<AnnClass> getClasses(AnnClass annClass, ClassLoader cl) throws ComponentGleanerException {
        assert annClass != null;

        List<AnnClass> classes = new ArrayList<AnnClass>();

        while(annClass!=null) {
            classes.add(annClass);
            if(annClass.getSuperName()!=null) {
              annClass = readClass(annClass.getSuperName(), cl);
            } else {
              break;
            }

            //
            // TODO: See if we need to include interfaces here too?
            //
        }

        return classes;
    }

    private ComponentRequirement findRequirement(final AnnField field, AnnClass annClass, ClassLoader cl) 
        throws ComponentGleanerException 
    {
        assert field != null;

        Requirement anno = field.getAnnotation(Requirement.class);
        
        if (anno == null) {
            return null;
        }

        String fieldType = field.getType();
        
        // TODO implement type resolution without loading classes
        Class<?> type;
        try {
          type = Class.forName(fieldType, false, cl);
        } catch (ClassNotFoundException ex) {
          // TODO Auto-generated catch block
          throw new ComponentGleanerException("Can't load class " + fieldType);
        }

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

        // TODO need to read default annotation values 
        // if (anno.role()==null || anno.role().isAssignableFrom(Object.class)) {
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

    private PlexusConfiguration findConfiguration(AnnField field, AnnClass c, ClassLoader cl) {
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
