/*
 * $Id$
 */

package org.codehaus.plexus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;

import org.codehaus.classworlds.ClassWorld;

import org.codehaus.plexus.component.repository.ComponentLookupException;
import org.codehaus.plexus.configuration.ConfigurationResourceException;

/**
 * Implementation of the <code>PlexusContainer</code> interface that can
 * be used as a component inside another container instance.
 * Currently uses the Avalon lifecycle methods.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class ComponentPlexusContainer
    implements PlexusContainer, Contextualizable, Configurable,
               Initializable, Startable
{
    /**
     * Parent <code>PlexusContainer</code>. That is, the
     * <code>PlexusContainer</code> that this component is in.
     */
    private PlexusContainer parentPlexus;

    /** Our own <code>PlexusContainer</code>. */
    private DefaultPlexusContainer plexusEmbedder
        = new DefaultPlexusContainer();

    private String configurationName;

    public Object lookup( String role )
        throws ComponentLookupException
    {
        if (plexusEmbedder.hasService( role ))
        {
            return plexusEmbedder.lookup( role );
        }
        if (parentPlexus != null)
        {
            return parentPlexus.lookup( role );
        }
        return plexusEmbedder.lookup( role );
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        if (plexusEmbedder.hasService( role, id ))
        {
            return plexusEmbedder.lookup( role, id );
        }
        if (parentPlexus != null)
        {
            return parentPlexus.lookup( role, id );
        }
        return plexusEmbedder.lookup( role, id );
    }

    public boolean hasService( String role )
    {
        if (plexusEmbedder.hasService( role ))
        {
            return true;
        }
        if (parentPlexus != null)
        {
            return parentPlexus.hasService( role );
        }
        return false;
    }

    public boolean hasService( String role, String id )
    {
        if (plexusEmbedder.hasService( role, id ))
        {
            return true;
        }
        if (parentPlexus != null)
        {
            return parentPlexus.hasService( role, id );
        }
        return false;
    }

    public void release( Object service )
    {
        plexusEmbedder.release( service );
        if (parentPlexus != null)
        {
            parentPlexus.release( service );
        }
    }

    public void suspend( Object component )
    {
        plexusEmbedder.suspend( component );
        if (parentPlexus != null)
        {
            parentPlexus.suspend( component );
        }
    }

    public void resume( Object component )
    {
        plexusEmbedder.resume( component );
        if (parentPlexus != null)
        {
            parentPlexus.resume( component );
        }
    }

    public void addContextValue( Object key, Object value )
    {
        plexusEmbedder.addContextValue( key, value );
    }

    public void setClassWorld( ClassWorld classWorld )
    {
        throw new UnsupportedOperationException( "setClassWorld is not supported for ComponentPlexusContainer" );
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        throw new UnsupportedOperationException( "setClassLoader is not supported for ComponentPlexusContainer" );
    }

    public void setConfigurationResource( Reader configuration )
        throws ConfigurationResourceException
    {
        throw new UnsupportedOperationException( "setConfigurationResource is not supported for ComponentPlexusContainer" );
    }

    public ClassLoader getClassLoader()
    {
        return plexusEmbedder.getClassLoader();
    }

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        configurationName = configuration.getChild( "configuration-name" ).getValue();
    }

    public void initialize()
        throws Exception
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream stream = loader.getResourceAsStream( configurationName );

        Reader r = new InputStreamReader( stream );

        plexusEmbedder.setConfigurationResource( r );

        plexusEmbedder.initialize();

        plexusEmbedder.addContextValue( PlexusConstants.PLEXUS_KEY, this );
    }

    public void start()
        throws Exception
    {
        plexusEmbedder.start();
    }

    public void stop()
        throws Exception
    {
        plexusEmbedder.dispose();
    }

    public void dispose()
        throws Exception
    {
        throw new UnsupportedOperationException( "dispose is not supported for ComponentPlexusContainer" );
    }
}
