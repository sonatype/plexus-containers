package org.codehaus.plexus.classloader;

import java.io.File;
import java.io.InputStream;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/** ClassLoading resource manager.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 */
public class DefaultResourceManager
    extends AbstractLogEnabled
    implements ResourceManager
{
    /** The classloader to use for loading resources and classes. */
    private ClassRealm classRealm;

    public DefaultResourceManager()
    {
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public void setClassRealm( ClassRealm classRealm )
    {
        this.classRealm = classRealm;
    }
    
    /**
     * @return Returns the classRealm.
     */
    public ClassRealm getClassRealm()
    {
        return classRealm;
    }
    
    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    /** Get a resource returned as a string. */
    public InputStream getResourceAsStream( String resource )
    {
        return getClassRealm().getClassLoader().getResourceAsStream( resource );
    }

    /** Perform configuration.
     *
     *  @param configuration The configuration.
     *
     *  @throws PlexusConfigurationException If an error occurs while attempting
     *          to perform configuration.
     */
    public void configure( PlexusConfiguration configuration )
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] resourceConfigs = configuration.getChildren();

        for ( int i = 0; i < resourceConfigs.length; ++i )
        {
            try
            {
                if ( resourceConfigs[i].getName().equals( "jar-repository" ) )
                {
                    addJarRepository( new File( resourceConfigs[i].getValue() ) );
                }
            }
            catch ( Exception e )
            {
                getLogger().error( "error configuring resource: " + resourceConfigs[i].getValue(), e );
            }
        }
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
        getClassRealm().addConstituent( jar.toURL() );
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
        else
        {
            throw new Exception( "The specified JAR repository doesn't exist or is not a directory." );
        }
    }
}
