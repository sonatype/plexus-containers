package org.codehaus.plexus.javadoc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Map;

import com.sun.tools.doclets.Taglet;

/**
 * Tagging a field with <tt>@plexus.requirement</tt> will tell plexus to inject the required component or
 * list of components before the component itself is started. Fields can be of the type of the Interface
 * defining the component you wish to be injected or of type <tt>java.util.List</tt> or <tt>java.util.Map</tt>.
 * <br/>
 * Fields of type <tt>java.lang.List</tt> will have a list of components injected whereas a field of
 * type <tt>java.lang.Map</tt> will have a mapping in the form of <tt>role-hint -&gt; component</tt>.
 * The <tt>role</tt> parameter is required if using a <tt>List</tt> or <tt>Map</tt> whereas a role-hint is
 * not allowed.
 * <table class="bodyTable">
 *   <tbody>
 *     <tr class="a">
 *       <td align="left"><b>Parameter</b></td>
 *       <td align="left"><b>Required</b></td>
 *       <td align="left"><b>Description</b></td>
 *     </tr>
 *     <tr class="b">
 *       <td align="left">role</td>
 *       <td align="left">No. Yes if the field is a <tt>List</tt> or a <tt>Map</tt></td>
 *       <td align="left">The role parameter is used to tell plexus what Component role you are interested in.
 *       For singleton fields their type is used as a default.</td>
 *     </tr>
 *     <tr class="a">
 *       <td align="left">role-hint</td>
 *       <td align="left">No. Not allowed if the field is a <tt>List</tt> or a <tt>Map</tt></td>
 *       <td align="left">Links to the role-hint defined by a component when looking up a component.
 *       Not allowed with <tt>List</tt> or <tt>Map</tt> fields, as they return all <tt>role-hint</tt>s.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @see <a href="http://plexus.codehaus.org/guides/developer-guide/appendices/javadoc-tags-reference.html">
 * http://plexus.codehaus.org/guides/developer-guide/appendices/javadoc-tags-reference.html</a>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class PlexusRequirementTaglet
    extends AbstractPlexusTaglet
{
    private static final String NAME = "plexus.requirement";

    private static final String HEADER = "Plexus requirement";

    /** {@inheritDoc} */
    public String getHeader()
    {
        return HEADER;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return NAME;
    }

    /** {@inheritDoc} */
    public boolean inConstructor()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean inField()
    {
        return true;
    }

    /** {@inheritDoc} */
    public boolean inMethod()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean inOverview()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean inPackage()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean inType()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isInlineTag()
    {
        return false;
    }

    /**
     * Register this Taglet.
     *
     * @param tagletMap the map to register this tag to.
     */
    public static void register( Map tagletMap )
    {
        PlexusRequirementTaglet tag = new PlexusRequirementTaglet();
        Taglet t = (Taglet) tagletMap.get( tag.getName() );
        if ( t != null )
        {
            tagletMap.remove( tag.getName() );
        }
        tagletMap.put( tag.getName(), tag );
    }
}
