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
 * The <tt>@plexus.configuration</tt> tags are used to mark fields in a class for configuration through
 * the <tt>components.xml</tt>.
 * <table class="bodyTable">
 *   <tbody>
 *     <tr class="a">
 *       <td align="left"><b>Parameter</b></td>
 *       <td align="left"><b>Required</b></td>
 *       <td align="left"><b>Description</b></td>
 *     </tr>
 *     <tr class="b">
 *       <td align="left">default-value</td>
 *       <td align="left">Currently</td>
 *       <td align="left">The default values are currently required for the <tt>&lt;configuration</tt>&gt;
 *       tag to be written to <tt>components.xml</tt>.</td>
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
public class PlexusConfigurationTaglet
    extends AbstractPlexusTaglet
{
    private static final String NAME = "plexus.configuration";

    private static final String HEADER = "Plexus configuration";

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
        PlexusConfigurationTaglet tag = new PlexusConfigurationTaglet();
        Taglet t = (Taglet) tagletMap.get( tag.getName() );
        if ( t != null )
        {
            tagletMap.remove( tag.getName() );
        }
        tagletMap.put( tag.getName(), tag );
    }
}
