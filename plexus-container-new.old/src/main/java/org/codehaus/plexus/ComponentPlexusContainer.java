/*
 * $Id$
 */

package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the <code>PlexusContainer</code> interface that can
 * be used as a component inside another container instance.
 * Currently uses the Avalon lifecycle methods.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class ComponentPlexusContainer
    implements PlexusContainer

    //, Contextualizable, Configurable,
    // Initializable, Startable
{

    public Map getComponentDescriptorMap( String s )
    {
        return null;
    }

    public ComponentDescriptor getComponentDescriptor( String s )
    {
        return null;
    }

    /**
     * <code>Configuration</code> element name: Plexus configuration resource.
     */
    public static final String PLEXUS_CONFIG = "plexus-config";

    /**
     * <code>Configuration</code> element name: Plexus context setting.
     */
    public static final String CONTEXT_VALUE = "context-value";

    /**
     * <code>Configuration</code> element name: Plexus context setting name.
     */
    public static final String CONTEXT_VALUE_NAME = "name";

    /**
     * <code>Configuration</code> element name: Plexus context setting value.
     */
    public static final String CONTEXT_VALUE_VALUE = "value";

    /**
     * Parent <code>PlexusContainer</code>. That is, the
     * <code>PlexusContainer</code> that this component is in.
     */
    private PlexusContainer parentPlexus;

    /** Our own <code>PlexusContainer</code>. */
    private DefaultPlexusContainer myPlexus = new DefaultPlexusContainer();

    private String configurationName;

    public Object lookup( String role )
        throws ComponentLookupException
    {
        if ( myPlexus.hasComponent( role ) )
        {
            return myPlexus.lookup( role );
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.lookup( role );
        }

        return myPlexus.lookup( role );
    }

    public Map lookupMap( String role )
        throws ComponentLookupException
    {
        if ( myPlexus.hasComponent( role ) )
        {
            return myPlexus.lookupMap( role );
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.lookupMap( role );
        }

        return myPlexus.lookupMap( role );
    }

    public List lookupList( String role )
        throws ComponentLookupException
    {
        if ( myPlexus.hasComponent( role ) )
        {
            return myPlexus.lookupList( role );
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.lookupList( role );
        }

        return myPlexus.lookupList( role );
    }

    public void releaseAll( Map components )
    {
        // Not exactly sure how to do this here.
    }

    public void releaseAll( List components )
    {
        // Not exactly sure how to do this here.
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        if ( myPlexus.hasComponent( role, id ) )
        {
            return myPlexus.lookup( role, id );
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.lookup( role, id );
        }

        return myPlexus.lookup( role, id );
    }

    public boolean hasComponent( String role )
    {
        if ( myPlexus.hasComponent( role ) )
        {
            return true;
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.hasComponent( role );
        }

        return false;
    }

    public boolean hasComponent( String role, String id )
    {
        if ( myPlexus.hasComponent( role, id ) )
        {
            return true;
        }

        if ( parentPlexus != null )
        {
            return parentPlexus.hasComponent( role, id );
        }

        return false;
    }

    public void release( Object service )
    {
        myPlexus.release( service );

        if ( parentPlexus != null )
        {
            parentPlexus.release( service );
        }
    }

    public void suspend( Object component )
    {
        myPlexus.suspend( component );
        if ( parentPlexus != null )
        {
            parentPlexus.suspend( component );
        }
    }

    public void resume( Object component )
    {
        myPlexus.resume( component );
        if ( parentPlexus != null )
        {
            parentPlexus.resume( component );
        }
    }

    public void addContextValue( Object key, Object value )
    {
        myPlexus.addContextValue( key, value );
    }

    public void setConfigurationResource( Reader configuration )
        throws PlexusConfigurationResourceException
    {
        throw new UnsupportedOperationException( "setConfigurationResource is not supported for ComponentPlexusContainer" );
    }

    public ClassLoader getClassLoader()
    {
        return myPlexus.getClassLoader();
    }

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void configure( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        configurationName = configuration.getChild( PLEXUS_CONFIG ).getValue();

        PlexusConfiguration[] contextValues = configuration.getChildren( CONTEXT_VALUE );

        for ( int i = 0; i < contextValues.length; i++ )
        {
            PlexusConfiguration c = contextValues[i];

            String name = c.getChild( CONTEXT_VALUE_NAME ).getValue();

            String value = c.getChild( CONTEXT_VALUE_VALUE ).getValue();

            addContextValue( name, value );
        }
    }

    public void initialize()
        throws Exception
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream stream = loader.getResourceAsStream( configurationName );

        Reader r = new InputStreamReader( stream );

        myPlexus.setConfigurationResource( r );

        myPlexus.initialize();

        myPlexus.addContextValue( PlexusConstants.PLEXUS_KEY, this );
    }

    public void start()
        throws Exception
    {
        myPlexus.start();
    }

    public void stop()
        throws Exception
    {
        myPlexus.dispose();
    }

    public void dispose()
        throws Exception
    {
        throw new UnsupportedOperationException( "dispose is not supported for ComponentPlexusContainer" );
    }
}
