package org.codehaus.plexus;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.DefaultContext;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

/**
 * @author Jason van Zyl
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class PlexusTestCase
    extends TestCase
{
    protected PlexusContainer container;

    protected Map context;

    private static String basedir;

    protected void setUp()
        throws Exception
    {
        basedir = getBasedir();

        // ----------------------------------------------------------------------------
        // Context Setup
        // ----------------------------------------------------------------------------

        context = new HashMap();

        context.put( "basedir", getBasedir() );

        customizeContext( new DefaultContext( context ) );

        boolean hasPlexusHome = context.containsKey( "plexus.home" );

        if ( !hasPlexusHome )
        {
            File f = getTestFile( "target/plexus-home" );

            if ( !f.isDirectory() )
            {
                f.mkdir();
            }

            context.put( "plexus.home", f.getAbsolutePath() );
        }

        // ----------------------------------------------------------------------------
        // Configuration
        // ----------------------------------------------------------------------------

        String config = getCustomConfigurationName();

        if ( config != null )
        {
            URL url = getClassLoader().getResource( config );

            if ( url != null )
            {
                container = new DefaultPlexusContainer( "test", context, url );
            }
            else
            {
                container = new DefaultPlexusContainer( "test", context, new File( config ) );
            }
        }
        else
        {
            URL url = getClassLoader().getResource( getConfigurationName( null ) );

            if ( url != null )
            {
                container = new DefaultPlexusContainer( "test", context, url );
            }
            else
            {
                container = new DefaultPlexusContainer( "test", context );
            }
        }
    }

    protected void customizeContext( Context context )
        throws Exception
    {
    }

    protected void tearDown()
        throws Exception
    {
        if ( container != null )
        {
            container.dispose();

            container = null;
        }
    }

    protected PlexusContainer getContainer()
    {
        return container;
    }

    protected InputStream getConfiguration()
        throws Exception
    {
        return getConfiguration( null );
    }

    protected InputStream getConfiguration( String subname )
        throws Exception
    {
        return getResourceAsStream( getConfigurationName( subname ) );
    }

    protected String getCustomConfigurationName()
    {
        return null;
    }

    protected String getConfigurationName( String subname )
        throws Exception
    {
        return getClass().getName().replace( '.', '/' ) + ".xml";
    }

    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream( resource );
    }

    protected ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    // ----------------------------------------------------------------------
    // Container access
    // ----------------------------------------------------------------------

    protected Object lookup( String componentKey )
        throws Exception
    {
        return getContainer().lookup( componentKey );
    }

    protected Object lookup( String role,
                             String id )
        throws Exception
    {
        return getContainer().lookup( role, id );
    }

    protected Object lookup( Class componentClass )
        throws Exception
    {
        return getContainer().lookup( componentClass );
    }

    protected Object lookup( Class role,
                             String id )
        throws Exception
    {
        return getContainer().lookup( role, id );
    }

    protected void release( Object component )
        throws Exception
    {
        getContainer().release( component );
    }

    // ----------------------------------------------------------------------
    // Helper methods for sub classes
    // ----------------------------------------------------------------------

    public static File getTestFile( String path )
    {
        return new File( getBasedir(), path );
    }

    public static File getTestFile( String basedir,
                                    String path )
    {
        File basedirFile = new File( basedir );

        if ( !basedirFile.isAbsolute() )
        {
            basedirFile = getTestFile( basedir );
        }

        return new File( basedirFile, path );
    }

    public static String getTestPath( String path )
    {
        return getTestFile( path ).getAbsolutePath();
    }

    public static String getTestPath( String basedir,
                                      String path )
    {
        return getTestFile( basedir, path ).getAbsolutePath();
    }

    public static String getBasedir()
    {
        if ( basedir != null )
        {
            return basedir;
        }

        basedir = System.getProperty( "basedir" );
        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

        return basedir;
    }

    public String getTestConfiguration()
    {
        return getTestConfiguration( getClass() );
    }

    public static String getTestConfiguration( Class clazz )
    {
        String s = clazz.getName().replace( '.', '/' );

        return s.substring( 0, s.indexOf( "$" ) ) + ".xml";
    }
}
