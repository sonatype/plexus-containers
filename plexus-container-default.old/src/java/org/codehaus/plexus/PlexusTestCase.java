package org.codehaus.plexus;

/* ----------------------------------------------------------------------------
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Plexus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ----------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 * ----------------------------------------------------------------------------
 */

import junit.framework.TestCase;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PlexusTestCase
    extends TestCase
{
    /** Plexus container to run test in. */
    DefaultPlexusContainer container;

    /**
     * Basedir for all file I/O. Important when running tests from
     * the reactor.
     */
    public String basedir = System.getProperty( "basedir" );

    /**
     * Constructor.
     *
     *  @param testName
     */
    public PlexusTestCase( String testName )
    {
        super( testName );
    }

    /**
     * Set up the test-case by starting the container.
     */
    public void setUp()
        throws Exception
    {
        // For testing we want to set the root directory so that context
        // values can retrieved without error.
        if ( basedir == null )
        {
            basedir = new File( "" ).getPath();
        }

        File f = new File( basedir, "target/plexus-home" );
        System.setProperty( "plexus.home", f.getAbsolutePath() );
        
        if ( !f.isDirectory() )
        {
            f.mkdir();
        }

        InputStream configuration = null;

        try
        {
            configuration = getCustomConfiguration();

            if ( configuration == null )
            {
                configuration = getConfiguration();
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Error with configuration:" );
            System.out.println( "configuration = " + configuration );
            fail( e.getMessage() );
        }

        if ( configuration == null )
        {
            throw new IllegalStateException( "The configuration for your plexus test case cannot be null. " );
        }

        container = new DefaultPlexusContainer();
        container.addContextValue( "basedir", basedir );
        container.addContextValue( "plexus.home", System.getProperty( "plexus.home" ) );
        container.setConfigurationResource( new InputStreamReader( configuration  ) );
        container.initialize();
        container.start();
    }

    public InputStream getCustomConfiguration()
        throws Exception
    {
        return null;
    }

    /**
     * Tear down the test-case by stopping the container container instance..
     */
    public void tearDown()
        throws Exception
    {
        container.dispose();
        container = null;
    }

    /**
     * Get the container container instance.
     *
     * @return The container instance.
     */
    protected DefaultPlexusContainer getContainer()
    {
        return container;
    }

    /**
     * Get the configuration for this test.
     *
     * @return Configuration for this test.
     *
     * @throws Exception If an error occurs retrieve the container configuration.
     */
    protected InputStream getConfiguration()
        throws Exception
    {
        return getConfiguration( null );
    }

    /**
     * Get the container configuration from an URL.
     *
     * @return The container configuration.
     */
    protected URL getConfigurationUrl()
    {
        String className = getClass().getName();
        String base = className.substring( className.lastIndexOf( "." ) + 1 );
        String config = base + ".xml";

        return getClass().getResource( config );
    }

    /** Retrieve the default Plexus configuration.
     *
     *  @return The configuration.
     */
    protected InputStream getConfiguration( String subname )
        throws Exception
    {
        String className = getClass().getName();
        String base = className.substring( className.lastIndexOf( "." ) + 1 );

        String config = null;

        if ( subname == null
            || subname.equals( "" ) )
        {
            config = base + ".xml";
        }
        else
        {
            config = base + "-" + subname + ".xml";
        }

        InputStream configStream = getResourceAsStream( config );

        return configStream;
    }

    /**
     *  Retrieve a test resource that is in the same package as the test case.
     *
     *  @param resource Resource to find.
     *
     *  @return The input stream or null if the resource couldn't be located.
     */
    protected InputStream getResourceAsStream( String resource )
    {
        return getClass().getResourceAsStream( resource );
    }

    /**
     * Get the classloader used by the testcase.
     *
     * @return The classloader used by test case.
     */
    protected ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    /**
     *
     * @param componentKey
     * @return
     * @throws Exception
     */
    protected Object lookup( String componentKey )
        throws Exception
    {
        return getContainer().getComponentRepository().lookup( componentKey );
    }

    protected Object lookup( String role, String id )
        throws Exception
    {
        return getContainer().getComponentRepository().lookup( role, id );
    }

    /** Retrieve a component by componentKey.
     *
     *  @param componentKey The componentKey.
     *
     *  @return A matching component.
     *
     *  @throws Exception If an error occurs.
     *  @deprecated use lookup( componentKey )
     */
    protected Object getComponent( String componentKey )
        throws Exception
    {
        return lookup( componentKey );
    }

    /**
     *
     * @param componentKey
     * @param id
     * @return
     * @throws Exception
     * @deprecated use lookup( componentKey, id )
     */
    protected Object getComponent( String componentKey, String id )
        throws Exception
    {
        return lookup( componentKey, id );
    }

    // Some convenience methods for retrieving files in tests.

    /**
     * Get test input file.
     *
     * @param path Path to test input file.
     */
    public String getTestFile( String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }

    /**
     * Get test input file.
     *
     * @param path Path to test input file.
     */
    public String getTestFile( String basedir, String path )
    {
        return new File( basedir, path ).getAbsolutePath();
    }
}
