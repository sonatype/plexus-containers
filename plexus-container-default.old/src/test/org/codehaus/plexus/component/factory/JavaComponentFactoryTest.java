package org.codehaus.plexus.component.factory;

import junit.framework.TestCase;
import org.codehaus.plexus.component.factory.java.JavaComponentFactory;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * 
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 *
 * @version $Id$
 */
public class JavaComponentFactoryTest
    extends TestCase
{
    public void testComponentCreation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );
        
        componentDescriptor.setImplementation( ComponentImplA.class.getName() );
        
        Object component = factory.newInstance( componentDescriptor, cl );

        assertNotNull( component );
    }
    
    
    public void testComponentCreationWithNotMatchingRoleAndImplemenation()
    throws Exception
{
    JavaComponentFactory factory = new JavaComponentFactory();

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    
    ComponentDescriptor componentDescriptor = new ComponentDescriptor();

    componentDescriptor.setRole( Component.class.getName() );
    
    componentDescriptor.setImplementation( ComponentImplB.class.getName() );
    
    try
    {
        factory.newInstance( componentDescriptor, cl );
        
        fail( "Component role and implementation does not match. Exception is expected" );
    }
    catch( Exception e )
    {
       //ok!
    }

    
}
}
