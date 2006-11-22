package org.codehaus.plexus.component.composition.setter;

import junit.framework.TestCase;

import java.util.Map;
import java.beans.PropertyDescriptor;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.composition.setter.SetterComponentComposer;
import org.codehaus.plexus.component.composition.setter.ChildComponent;
import org.codehaus.plexus.component.composition.ComponentA;

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
