package org.codehaus.plexus.classloader;

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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.Expand;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/** ClassLoading resource manager.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 */
public class DefaultResourceManager
    extends AbstractLogEnabled
    implements Configurable, ResourceManager
{
    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** The classloader to use for loading resources and classes. */
    private PlexusClassLoader plexusClassLoader;

    /** Parent classloader. */
    private ClassLoader classLoader;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    public DefaultResourceManager()
    {
    }

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        plexusClassLoader = new PlexusClassLoader( classLoader );
    }

    /** Retrieve the resource-loading <code>ClassLoader</code>.
     *
     *  @return The class-loader.
     */
    public PlexusClassLoader getPlexusClassLoader()
    {
        return this.plexusClassLoader;
    }

    public DefaultResourceManager createChild( String id )
    {
        DefaultResourceManager child = new DefaultResourceManager();
        child.setClassLoader( getPlexusClassLoader() );
        child.enableLogging( getLogger() );
        return child;
    }

    /** Add a directory resource.
     *
     *  @param directory The directory.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addDirectoryResource( String directory )
        throws Exception
    {
        addDirectoryResource( new File( directory ) );
    }

    /** Add a directory resource.
     *
     *  @param directory The directory.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addDirectoryResource( File directory )
        throws Exception
    {
        getPlexusClassLoader().addURL( directory.toURL() );
        getLogger().info( "added directory resource; " + directory.getPath() );
    }

    /** Add a jar resource.
     *
     *  @param jar The jar.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addJarResource( String jar )
        throws Exception
    {
        addJarResource( new File( jar ) );
    }

    /** Add a jar resource.
     *
     *  @param jar The jar.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addJarResource( File jar )
        throws Exception
    {
        getPlexusClassLoader().addURL( jar.toURL() );
        getLogger().info( "added jar resource: " + jar.getPath() );
    }

    /** Add a component jar resource.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addComponentResourceJar( String component )
        throws Exception
    {
        addComponentResourceJar( new File( component ) );
    }

    /** Add a component jar resource.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addComponentResourceJar( File component )
        throws Exception
    {
        Expand expand = new Expand();
        expand.setSrc( component );
        expand.setDest( component.getParentFile() );
        expand.setOverwrite( false );
        expand.execute();
    }

    /** Add a URL resource.
     *
     *  @param url The URL.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addUrlResource( String url )
        throws Exception
    {
        addUrlResource( new URL( url ) );
    }

    /** Add a URL resource.
     *
     *  @param url The URL.
     *
     *  @throws Exception If an error occurs while adding the resource.
     */
    public void addUrlResource( URL url )
        throws Exception
    {
        getPlexusClassLoader().addURL( url );
        getLogger().info( "added url resource; " + url.toExternalForm() );
    }

    /**
     * Get the available URLs from the underlying classloader.
     */
    public URL[] getURLs()
    {
        return getPlexusClassLoader().getURLs();
    }

    /** Get a resource returned as a string. */
    public InputStream getResourceAsStream( String resource )
    {
        return getPlexusClassLoader().getResourceAsStream( resource );
    }

    /** Perform configuration.
     *
     *  @param configuration The configuration.
     *
     *  @throws ConfigurationException If an error occurs while attempting
     *          to perform configuration.
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
        Configuration[] resourceConfigs = configuration.getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                if ( resourceConfigs[i].getName().equals( "directory" ) )
                {
                    addDirectoryResource( resourceConfigs[i].getValue() );
                }
                else if ( resourceConfigs[i].getName().equals( "jar" ) )
                {
                    addJarResource( resourceConfigs[i].getValue() );
                }
                else if ( resourceConfigs[i].getName().equals( "jar-repository" ) )
                {
                    addJarRepository( resourceConfigs[i].getValue() );
                }
                else if ( resourceConfigs[i].getName().equals( "url" ) )
                {
                    addUrlResource( resourceConfigs[i].getValue() );
                }
                else if ( resourceConfigs[i].getName().equals( "component" ) )
                {
                    addComponentResourceJar( resourceConfigs[i].getValue() );
                }
                else
                {
                    getLogger().warn( "unknown resource type: " + resourceConfigs[i].getName() );
                }
            }
            catch ( Exception e )
            {
                throw new ConfigurationException( "error configuring resource: " + resourceConfigs[i].getValue(),
                                                  e );
            }
        }
    }

    /**
     * Add a new repository to the set of places this ClassLoader can look for
     * classes to be loaded.
     *
     * @param repository Name of a source of classes to be loaded, such as a
     *      directory pathname, a JAR file pathname, or a ZIP file pathname. The
     *      parameter must be in the form of an URL.
     * @exception IllegalArgumentException if the specified repository is
     *      invalid or does not exist
     */
    public void addJarRepository( String repository )
        throws Exception
    {
        addJarRepository( new File( repository ) );
    }

    /**
     * Add a new repository to the set of places this ClassLoader can look for
     * classes to be loaded.
     *
     * @param repository Name of a source of classes to be loaded, such as a
     *      directory pathname, a JAR file pathname, or a ZIP file pathname. The
     *      parameter must be in the form of an URL.
     * @exception IllegalArgumentException if the specified repository is
     *      invalid or does not exist
     */
    public void addJarRepository( File repository )
        throws Exception
    {
        if ( repository.exists() && repository.isDirectory() )
        {
            File[] jars = repository.listFiles();

            for ( int j = 0; j < jars.length; j++ )
            {
                if ( jars[j].getAbsolutePath().endsWith( ".jar" ) )
                {
                    addJarResource( jars[j] );
                }
            }
        }
    }
}
