package org.codehaus.plexus.component.factory;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.classworlds.ClassRealm;

/** A <code>ServiceFactory</code> is responsible for instantiating a component.
 *
 *  @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *  @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 *
 *  @version $Id$
 */
public interface ComponentFactory
{
    /** Component role. */
    static String ROLE = ComponentFactory.class.getName();

    String getId();

    Object newInstance( ComponentDescriptor componentDescriptor, ClassRealm classRealm )
        throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
