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
        if (myPlexus.hasService( role ))
        {
            return myPlexus.lookup( role );
        }
        if (parentPlexus != null)
        {
            return parentPlexus.lookup( role );
        }
        return myPlexus.lookup( role );
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        if (myPlexus.hasService( role, id ))
        {
            return myPlexus.lookup( role, id );
        }
        if (parentPlexus != null)
        {
            return parentPlexus.lookup( role, id );
        }
        return myPlexus.lookup( role, id );
    }

    public boolean hasService( String role )
    {
        if (myPlexus.hasService( role ))
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
        if (myPlexus.hasService( role, id ))
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
        myPlexus.release( service );
        if (parentPlexus != null)
        {
            parentPlexus.release( service );
        }
    }

    public void suspend( Object component )
    {
        myPlexus.suspend( component );
        if (parentPlexus != null)
        {
            parentPlexus.suspend( component );
        }
    }

    public void resume( Object component )
    {
        myPlexus.resume( component );
        if (parentPlexus != null)
        {
            parentPlexus.resume( component );
        }
    }

    public void addContextValue( Object key, Object value )
    {
        myPlexus.addContextValue( key, value );
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
        return myPlexus.getClassLoader();
    }

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        configurationName = configuration.getChild( PLEXUS_CONFIG ).getValue();

        Configuration[] contextValues = configuration.getChildren( CONTEXT_VALUE );

        for ( int i = 0; i < contextValues.length; i++ )
        {
            Configuration c = contextValues[i];

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
