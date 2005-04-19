package org.codehaus.plexus;

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

import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;

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
        throws InitializationException
    {
        myPlexus = new DefaultPlexusContainer();

        myPlexus.setParentPlexusContainer( parentPlexus );

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        InputStream stream = loader.getResourceAsStream( plexusConfig );

        Reader r = new InputStreamReader( stream );

        try
        {
            myPlexus.setConfigurationResource( r );
        }
        catch ( PlexusConfigurationResourceException e )
        {
            throw new InitializationException( "Unable to initialize container configuration", e );
        }

        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String name = (String) i.next();

                myPlexus.addContextValue( name, contextValues.getProperty( name ) );
            }
        }

        try
        {
            myPlexus.initialize();
        }
        catch ( PlexusContainerException e )
        {
            throw new InitializationException( "Error initializing container", e );
        }
    }

    public void start()
        throws StartingException
    {
        try
        {
            myPlexus.start();
        }
        catch ( PlexusContainerException e )
        {
            throw new StartingException( "Error starting container", e );
        }
    }

    public void stop()
    {
        myPlexus.dispose();
    }

    public PlexusContainer[] getManagedContainers()
    {
        return new PlexusContainer[] { myPlexus };
    }
}
