package org.codehaus.plexus.component.composition;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
