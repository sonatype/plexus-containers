package org.codehaus.plexus.hierarchy;


import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

/**
 * Simple implementation of the {@link TestService} Component interface.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public class
    TestServiceImpl
    implements TestService//, Contextualizable, Configurable
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

    public void configure( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        knownValue = config.getChild( "known-value" ).getValue();
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
        PlexusContainer sibling = (PlexusContainer) parentPlexus.lookup( PlexusContainer.ROLE, id );

        TestService service = (TestService) sibling.lookup( TestService.ROLE );

        return service.getKnownValue();
    }
}
