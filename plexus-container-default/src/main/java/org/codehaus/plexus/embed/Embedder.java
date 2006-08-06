package org.codehaus.plexus.embed;

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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.classworlds.ClassWorld;

import java.util.Map;

public class Embedder
    implements PlexusEmbedder
{
    protected static final String DEFAULT_CONTAINER_NAME = "embedder";

    private final MutablePlexusContainer container;

    public Embedder()
        throws EmbedderException
    {
        this( null, null );
    }

    public Embedder( Map context, String configuration )
        throws EmbedderException
    {
        this( context, configuration, null );
    }

    public Embedder( Map context, String configuration, ClassWorld classWorld )
        throws EmbedderException
    {
        try
        {
            container = new DefaultPlexusContainer( DEFAULT_CONTAINER_NAME, context, configuration, classWorld );
        }
        catch ( PlexusContainerException e )
        {
            throw new EmbedderException( "Error creating embedder.", e );
        }
    }

    public synchronized PlexusContainer getContainer()
    {
        return container;
    }

    public Object lookup( String role )
        throws ComponentLookupException
    {
        return getContainer().lookup( role );
    }

    public Object lookup( String role, String id )
        throws ComponentLookupException
    {
        return getContainer().lookup( role, id );
    }

    public boolean hasComponent( String role )
    {
        return getContainer().hasComponent( role );
    }

    public boolean hasComponent( String role, String id )
    {
        return getContainer().hasComponent( role, id );
    }

    public void release( Object service )
        throws ComponentLifecycleException
    {
        getContainer().release( service );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setLoggerManager( LoggerManager loggerManager )
    {
        container.setLoggerManager( loggerManager );
    }

    public synchronized void stop()
    {
        container.dispose();
    }
}
