package org.codehaus.plexus.component.factory.nonjava;

import org.codehaus.plexus.component.factory.ComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.PlexusContainer;

/** @author Jason van Zyl */
public class NonJavaComponentFactory
    implements ComponentFactory
{
    public String getId()
    {
        return "nonjava";
    }

    public Object newInstance( ComponentDescriptor componentDescriptor,
                               ClassRealm classRealm,
                               PlexusContainer container )
        throws ComponentInstantiationException
    {
        return "component";
    }
}
