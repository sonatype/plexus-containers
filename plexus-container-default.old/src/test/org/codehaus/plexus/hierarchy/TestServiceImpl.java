package org.codehaus.plexus.hierarchy;


import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerManager;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Simple implementation of the {@link TestService} Component interface.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public class
    TestServiceImpl
    implements TestService, Contextualizable
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

        TestService service = (TestService) siblingContainer.lookup( TestService.ROLE );

        return service.getKnownValue();
    }
}
