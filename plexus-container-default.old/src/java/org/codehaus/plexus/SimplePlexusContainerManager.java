/*
 * $Id$
 */

package org.codehaus.plexus;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

/**
 * SimplePlexusContainerManager
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public class SimplePlexusContainerManager
    implements PlexusContainerManager,
               Contextualizable, Initializable, Startable
{
    /**
     * Parent <code>PlexusContainer</code>. That is, the
     * <code>PlexusContainer</code> that this component is in.
     */
    private PlexusContainer parentPlexus;

    /** Our own <code>PlexusContainer</code>. */
    private DefaultPlexusContainer myPlexus;

    private String plexusConfig;

    private Properties contextValues;

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws Exception
    {
        myPlexus = new DefaultPlexusContainer();

        myPlexus.setParentPlexusContainer( parentPlexus );

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream stream = loader.getResourceAsStream( plexusConfig );

        Reader r = new InputStreamReader( stream );

        myPlexus.setConfigurationResource( r );

        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String name = (String) i.next();

                myPlexus.addContextValue( name, contextValues.getProperty( name ) );
            }
        }

        myPlexus.initialize();
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

    public PlexusContainer[] getManagedContainers()
    {
        return new PlexusContainer[] { myPlexus };
    }
}
