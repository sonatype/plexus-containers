package org.codehaus.plexus.component.configurator;

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


/**
 * Listen for configuration changes on an object.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public interface ConfigurationListener
{
    /**
     * Notify the listener that a field has been set using its autowire.
     * @param fieldName the field
     * @param value the value set
     * @param target the target object
     */
    void notifyFieldChangeUsingSetter( String fieldName, Object value, Object target );

    /**
     * Notify the listener that a field has been set using private field injection.
     * @param fieldName the field
     * @param value the value set
     * @param target the target object
     */
    void notifyFieldChangeUsingReflection( String fieldName, Object value, Object target );
}
