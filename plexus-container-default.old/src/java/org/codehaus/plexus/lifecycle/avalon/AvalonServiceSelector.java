package org.codehaus.plexus.lifecycle.avalon;

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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

/**
 * PlexusServiceSelector selects components from the PlexusServiceBroker based
 * on their id.
 *
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 11, 2003
 */
public class AvalonServiceSelector
    implements ServiceSelector, Serviceable, Configurable
{
    /** Service Manager. */
    private ServiceManager serviceManager;

    /** Role being selected. */
    private String selectableRole;

    /** Selectable role configuration tag. */
    public final static String SELECTABLE_ROLE_KEY = "selectable-role";

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#select(java.lang.Object)
     */
    public Object select( Object hint )
        throws ServiceException
    {
        return serviceManager.lookup( selectableRole + hint );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#isSelectable(java.lang.Object)
     */
    public boolean isSelectable( Object hint )
    {
        return serviceManager.hasService( selectableRole + hint );
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceSelector#release(java.lang.Object)
     */
    public void release( Object component )
    {
        serviceManager.release( component );
    }

    /**
     * @see Serviceable#service(ServiceManager)
     */
    public void service( ServiceManager serviceManager )
        throws ServiceException
    {
        this.serviceManager = serviceManager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.codehaus.avalon.framework.configuration.Configuration)
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        selectableRole = configuration.getAttribute( SELECTABLE_ROLE_KEY );
    }
}
