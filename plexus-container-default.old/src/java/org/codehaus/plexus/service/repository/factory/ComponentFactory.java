package org.codehaus.plexus.service.repository.factory;

/** A <code>ServiceFactory</code> is responsible for instantiating a service.
 *
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 *  @version $Id$
 */
public interface ComponentFactory
{
    /** Component role. */
    static String ROLE = ComponentFactory.class.getName();

    Object newInstance( String name, ClassLoader classLoader )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
