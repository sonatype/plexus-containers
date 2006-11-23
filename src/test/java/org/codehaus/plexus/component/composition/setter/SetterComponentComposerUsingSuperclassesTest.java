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
import org.codehaus.plexus.component.repository.ComponentDescriptor;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class SetterComponentComposerUsingSuperclassesTest
    extends TestCase
{
    public void testSetterDiscoveryWhereSetterResidesInTheSuperclass()
        throws Exception
    {
        SetterComponentComposer composer = new SetterComponentComposer();

        ComponentDescriptor cd = new ComponentDescriptor();

        Object component = new ChildComponent();

        cd.setRole( component.getClass().getName()  );

        Map context = composer.createCompositionContext( component, cd );

        PropertyDescriptor[] pds = (PropertyDescriptor[]) context.get( SetterComponentComposer.PROPERTY_DESCRIPTORS );

        boolean setterFound = false;

        for ( int i = 0; i < pds.length; i++ )
        {
            PropertyDescriptor pd = pds[i];

            Class clazz = pd.getPropertyType();

            if ( clazz.isAssignableFrom( ComponentA.class ) )
            {
                setterFound = true;
            }
        }

        if ( !setterFound )
        {
            fail( "Could not find setter of type " + ComponentA.class + " ." );
        }
    }
}
