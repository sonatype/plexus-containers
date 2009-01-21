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

import java.util.Collection;
import java.util.Map;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * Support for component gleaner implementations.
 *
 * @version $Id$
 */
public abstract class ComponentGleanerSupport
{
    private static final String EMPTY_STRING = "";

    protected String filterEmptyAsNull(final String value) {
        if (value == null) {
            return null;
        } else if (EMPTY_STRING.equals(value.trim())) {
            return null;
        } else {
            return value;
        }
    }

    protected boolean isRequirementListType(final Class<?> type) {
        // assert type != null;

        return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    protected void addChildConfiguration(final ComponentDescriptor<?> component, final PlexusConfiguration config) {
        // assert component != null;
        // assert config != null;

        if (!component.hasConfiguration()) {
            component.setConfiguration(new XmlPlexusConfiguration("configuration"));
        }

        component.getConfiguration().addChild(config);
    }

    protected String deHump(final String string) {
        // assert string != null;

        StringBuffer buff = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {
            if (i != 0 && Character.isUpperCase(string.charAt(i))) {
                buff.append('-');
            }

            buff.append(string.charAt(i));
        }

        return buff.toString().trim().toLowerCase();
    }
}