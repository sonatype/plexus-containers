package org.codehaus.plexus.component.composition;

import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultComponentComposerUsingSuperclassesTest
    extends TestCase
{
    public void testGetFieldByTypeWhereFieldResidesInTheSuperclass()
        throws Exception
    {
        FieldComponentComposer composer = new FieldComponentComposer();

        DefaultComponent component = new DefaultComponent();

        Field fieldA = composer.getFieldByType( component, ComponentA.class, null );

        assertEquals( ComponentA.class, fieldA.getType() );
    }
    
    public void testGetFieldByNameWhereFieldResidesInTheSuperclass()
    throws Exception
    {
        FieldComponentComposer composer = new FieldComponentComposer();

        DefaultComponent component = new DefaultComponent();
        
        Field fieldA = composer.getFieldByName( component, "componentA", null );

        assertEquals( "componentA", fieldA.getName() );
    }
    
}
