package org.codehaus.plexus.component.discovery;

import java.util.List;
import java.io.File;

import junit.framework.TestCase;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.ClassRealm;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ComponentDiscovererTest
    extends TestCase
{
    public void testDefaultComponentDiscoverer()
        throws Exception
    {
        ComponentDiscoverer componentDiscoverer = new DefaultComponentDiscoverer();

        componentDiscoverer.setManager( new DefaultComponentDiscovererManager() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm core = classWorld.newRealm( "core" );

        core.addConstituent( new File( System.getProperty( "basedir"), "target/test-classes").toURL() );

        List componentSetDescriptors = componentDiscoverer.findComponents( core );

        System.out.println( componentSetDescriptors.get( 0 ) );

        assertEquals( 1, componentSetDescriptors.size() );

        assertEquals( ComponentSetDescriptor.class.getName(), componentSetDescriptors.get( 0 ).getClass().getName() );

        ComponentSetDescriptor componentSet = (ComponentSetDescriptor) componentSetDescriptors.get( 0 );

        List components = componentSet.getComponents();

        ComponentDescriptor cd = (ComponentDescriptor) components.get( 0 );

        assertEquals( "org.codehaus.plexus.component.discovery.DiscoveredComponent", cd.getRole() );

        assertEquals( "org.codehaus.plexus.component.discovery.DefaultDiscoveredComponent", cd.getImplementation() );
    }
}
