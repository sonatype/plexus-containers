package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SetterComponentComposerTest
        extends TestCase
{
    public void testGetPropertyByName() throws IntrospectionException
    {
        ComponentF componentF = new ComponentF();

        BeanInfo beanInfo = Introspector.getBeanInfo( componentF.getClass() );

        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        final SetterComponentComposer composer = new SetterComponentComposer();

        try
        {
            final PropertyDescriptor propertyA = composer.getPropertyDescriptorByName( "componentA", propertyDescriptors );

            assertEquals( ComponentA.class, propertyA.getPropertyType() );

            final PropertyDescriptor propertyB = composer.getPropertyDescriptorByName( "componentB", propertyDescriptors );

            assertEquals( ComponentB.class, propertyB.getPropertyType() );

            final PropertyDescriptor propertyC = composer.getPropertyDescriptorByName( "componentC", propertyDescriptors );

            assertTrue( propertyC.getPropertyType().isArray() );
        }

        catch ( Exception e )
        {
            e.printStackTrace();

            fail( e.getMessage() );

        }
    }


    public void testGetPropertyByType()
    {

        final SetterComponentComposer composer = new SetterComponentComposer();

        try
        {
            final ComponentF componentF = new ComponentF();

            BeanInfo beanInfo = Introspector.getBeanInfo( componentF.getClass() );

            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            final PropertyDescriptor propertyA = composer.getPropertyDescriptorByType( ComponentA.class.getName(), propertyDescriptors );

            assertNotNull( propertyA );
            
            assertEquals( ComponentA.class, propertyA.getPropertyType() );

            final PropertyDescriptor propertyB = composer.getPropertyDescriptorByType( ComponentB.class.getName(), propertyDescriptors );

            assertEquals( ComponentB.class, propertyB.getPropertyType() );

            final PropertyDescriptor propertyC = composer.getPropertyDescriptorByType( ComponentC.class.getName(), propertyDescriptors );

            assertTrue( propertyC.getPropertyType().isArray() );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            
            fail( e.getMessage() );

        }
    }


}
