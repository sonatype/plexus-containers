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
 * The <tt>@plexus.component</tt> tag is used to show that the class it annotates is a plexus component.
 * This will add a <tt>&lt;component</tt>&gt; element to the <tt>&lt;components</tt>&gt; in components.xml.
 * The following parameters are available, but remember the <tt>role</tt> is required.
 * The text in the javadoc tag describing this class is copied into the component's <tt>&lt;description</tt>&gt; tag.
 * <table class="bodyTable">
 *   <tbody>
 *     <tr class="a">
 *       <td align="left"><b>Parameter</b></td>
 *       <td align="left"><b>Required</b></td>
 *       <td align="left"><b>Description</b></td>
 *     </tr>
 *     <tr class="b">
 *       <td align="left">role</td>
 *       <td align="left">Yes</td>
 *       <td align="left">The role that this class provides an implementation for (usually the class name
 *       of an implemented Interface</td>
 *     </tr>
 *     <tr class="a">
 *       <td align="left">role-hint</td>
 *       <td align="left">No</td>
 *       <td align="left">The hints are used to differentiate multiple implementations of the same role</td>
 *     </tr>
 *       <tr class="b">
 *       <td align="left">version</td>
 *       <td align="left">No</td>
 *       <td align="left">Set the version of the component</td>
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
public class PlexusComponentTaglet
    extends AbstractPlexusTaglet
{
    private static final String NAME = "plexus.component";

    private static final String HEADER = "Plexus component";

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
        return false;
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
        return true;
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
        PlexusComponentTaglet tag = new PlexusComponentTaglet();
        Taglet t = (Taglet) tagletMap.get( tag.getName() );
        if ( t != null )
        {
            tagletMap.remove( tag.getName() );
        }
        tagletMap.put( tag.getName(), tag );
    }
}
