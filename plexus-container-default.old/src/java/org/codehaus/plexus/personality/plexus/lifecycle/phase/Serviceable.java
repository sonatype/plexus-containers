package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/**
 * Indicates that a class wants a hold on a ServiceLocator.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Serviceable
{
	void service( ServiceLocator locator );
}
