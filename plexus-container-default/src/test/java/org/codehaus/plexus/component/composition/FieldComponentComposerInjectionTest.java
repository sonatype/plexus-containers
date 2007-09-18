package org.codehaus.plexus.component.composition;

import org.codehaus.plexus.PlexusTestCase;

public class FieldComponentComposerInjectionTest extends PlexusTestCase {
	public void testArrayInject() throws Exception {
		ComponentF componentF = (ComponentF) lookup(ComponentF.class);

		assertNotNull(componentF);
		assertNotNull(componentF.getComponentC());
		assertEquals(2, componentF.getComponentC().length);
	}
}
