package org.codehaus.plexus.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/** Simple <code>URLClassLoader</code> that exposes the ability to dynamically
 *  add URLs after construct.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 */
public class PlexusClassLoader
    extends URLClassLoader
{
    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /** Construct.
     *
     *  @param parent Parent classloader.
     */
    public PlexusClassLoader( ClassLoader parent )
    {
        super( new URL[0], parent );
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     java.net.URLClassLoader
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Add a URL to the search path.
     *
     *  @param url The url.
     */
    public void addURL( URL url )
    {
        super.addURL( url );
    }
}
