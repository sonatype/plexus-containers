package org.codehaus.plexus.component.composition.setter;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.component.composition.SetterComponentComposer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class AutowireSetterComponentComposerTest
    extends PlexusTestCase
{
    public void testSetterAutowire()
        throws Exception
    {
        SetterComponentComposer composer = new SetterComponentComposer();

        Autowire autowire = new Autowire();               

        composer.assembleComponent( autowire, null, getContainer() );
    }
}
