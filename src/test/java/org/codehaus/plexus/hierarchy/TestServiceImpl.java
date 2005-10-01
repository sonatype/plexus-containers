package org.codehaus.plexus.hierarchy;


/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Simple implementation of the {@link PlexusTestService} Component interface.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public class
    TestServiceImpl
    implements PlexusTestService, Contextualizable
{
    private PlexusContainer parentPlexus;

    private String plexusName;

    private String knownValue;

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

        plexusName = (String) context.get( "plexus-name" );
    }

    public String getPlexusName()
    {
        return plexusName;
    }

    public String getKnownValue()
    {
        return knownValue;
    }

    public String getSiblingKnownValue( String id )
        throws ComponentLookupException
    {
        PlexusContainerManager manager;

        if ( id != null )
        {
            manager = (PlexusContainerManager) parentPlexus.lookup( PlexusContainerManager.ROLE, id );
        }
        else
        {
            manager = (PlexusContainerManager) parentPlexus.lookup( PlexusContainerManager.ROLE );
        }

        PlexusContainer siblingContainer = manager.getManagedContainers()[0];

        PlexusTestService service = (PlexusTestService) siblingContainer.lookup( PlexusTestService.ROLE );

        return service.getKnownValue();
    }
}
