package org.codehaus.plexus;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * ----------------------------------------------------------------------------
 */

public class PlexusConstants
{
    /** Default selector tag. */
    public static final String DEFAULT_SELECTOR = "default-selector";

    /** Selector tag. */
    public static final String SELECTOR = "selector";

    /** Selectors tag. */
    public static final String SELECTORS = "selectors";

    /** Default selector implementation tag. */
    public static final String DEFAULT_SERVICE_SELECTOR_IMPL = "org.codehaus.plexus.service.PlexusServiceSelector";

    /** Selector impl suffix tag. */
    public static final String SELECTOR_IMPL_SUFFIX = "Selector";

    /** Component tag. */
    public static final String COMPONENT = "component";

    /** Components tag. */
    public static final String COMPONENTS = "components";

    /** Id tag. */
    public static final String ID = "id";

    /** Role tag. */
    public static final String ROLE = "role";

    /** Roles tag. */
    public static final String ROLES = "roles";

    /** Implementation tag. */
    public static final String IMPL_KEY = "implementation";

    /** Configuration tag. */
    public static final String CONFIGURATION = "configuration";

    /** Key to retrieve the main Plexus object **/
    public static final String PLEXUS_KEY = "plexus";

    /** Key to retrieve the <code>File</code> plexus root, if any. */
    public static final String ROOT_KEY = "plexus:root";

    /** Key to retrieve the <code>File</code> plexus root,
     if any. */
    public static final String ROOT_URL_KEY = "plexus:root-url";

    /** Key to retrieve the <code>File</code> plexus deployment root, if
     any. */
    public static final String DEPLOY_KEY = "plexus:deploy";

    /** Key to retrieve the <code>File</code> plexus working-directory
     root, if any. */
    public static final String WORK_KEY = "plexus:work";

    /** Key to retrieve the <code>File</code> plexus runtime root, if
     any. */
    public static final String RUN_KEY = "plexus:run";

    /** Key to retrieve the <code>File</code> plexus runtime root, if
     any. */
    public static final String RESOURCE_MANAGER_KEY = "plexus:resource-manager";

    /** Common classloader */
    public static final String COMMON_CLASSLOADER = "common.classloader";
}
