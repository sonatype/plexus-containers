package org.codehaus.plexus.component.factory;

/** A <code>ServiceFactory</code> is responsible for instantiating a component.
 *
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 *  @version $Id$
 */
public interface ComponentFactory
{
    /** Component role. */
    static String ROLE = ComponentFactory.class.getName();

    String getId();

    Object newInstance( String name, ClassLoader classLoader )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
