package org.codehaus.plexus.component.composition.setter;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;
import org.codehaus.plexus.component.composition.ComponentA;
import org.codehaus.plexus.component.composition.ComponentB;
import org.codehaus.plexus.component.composition.ComponentC;
import org.codehaus.plexus.component.composition.ComponentF;

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
    public void testGetPropertyByName()
        throws IntrospectionException
    {
        ComponentF componentF = new ComponentF();

        BeanInfo beanInfo = Introspector.getBeanInfo( componentF.getClass() );

        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        final SetterComponentComposer composer = new SetterComponentComposer();

        final PropertyDescriptor propertyA = composer.getPropertyDescriptorByName( "componentA", propertyDescriptors );

        assertEquals( ComponentA.class, propertyA.getPropertyType() );

        final PropertyDescriptor propertyB = composer.getPropertyDescriptorByName( "componentB", propertyDescriptors );

        assertEquals( ComponentB.class, propertyB.getPropertyType() );

        final PropertyDescriptor propertyC = composer.getPropertyDescriptorByName( "componentC", propertyDescriptors );

        assertTrue( propertyC.getPropertyType().isArray() );
    }


    public void testGetPropertyByType()
        throws IntrospectionException
    {

        final SetterComponentComposer composer = new SetterComponentComposer();

        final ComponentF componentF = new ComponentF();

        BeanInfo beanInfo = Introspector.getBeanInfo( componentF.getClass() );

        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        final PropertyDescriptor propertyA = composer.getPropertyDescriptorByType( ComponentA.class.getName(),
                                                                                   propertyDescriptors );

        assertNotNull( propertyA );

        assertEquals( ComponentA.class, propertyA.getPropertyType() );

        final PropertyDescriptor propertyB = composer.getPropertyDescriptorByType( ComponentB.class.getName(),
                                                                                   propertyDescriptors );

        assertEquals( ComponentB.class, propertyB.getPropertyType() );

        final PropertyDescriptor propertyC = composer.getPropertyDescriptorByType( ComponentC.class.getName(),
                                                                                   propertyDescriptors );

        assertTrue( propertyC.getPropertyType().isArray() );
    }
}
