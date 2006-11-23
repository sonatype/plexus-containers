package org.codehaus.plexus.configuration;

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

public interface PlexusConfiguration
{
    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    String getName();

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    String getValue()
        throws PlexusConfigurationException;

    String getValue( String defaultValue );

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    String[] getAttributeNames();

    String getAttribute( String paramName )
        throws PlexusConfigurationException;

    String getAttribute( String name, String defaultValue );

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    PlexusConfiguration getChild( String child );

    PlexusConfiguration getChild( int i );

    PlexusConfiguration getChild( String child, boolean createChild );

    PlexusConfiguration[] getChildren();

    PlexusConfiguration[] getChildren( String name );

    void addChild( PlexusConfiguration configuration );

    int getChildCount();
}
