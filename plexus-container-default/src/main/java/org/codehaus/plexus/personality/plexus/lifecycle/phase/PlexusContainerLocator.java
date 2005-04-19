package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.List;
import java.util.Map;

/**
 * A ServiceLocator for PlexusContainer.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusContainerLocator
	implements ServiceLocator
{
	private PlexusContainer container;
    
    public PlexusContainerLocator( PlexusContainer container )
    {
        this.container = container;
    }
    
	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#lookup(java.lang.String)
	 */
	public Object lookup(String componentKey) throws ComponentLookupException
	{
		return container.lookup(componentKey);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#lookup(java.lang.String, java.lang.String)
	 */
	public Object lookup(String role, String roleHint) throws ComponentLookupException
	{
		return container.lookup(role, roleHint);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#lookupMap(java.lang.String)
	 */
	public Map lookupMap(String role) throws ComponentLookupException
	{
		return container.lookupMap(role);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#lookupList(java.lang.String)
	 */
	public List lookupList(String role) throws ComponentLookupException
	{
		return container.lookupList(role);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#release(java.lang.Object)
	 */
	public void release(Object component)
        throws ComponentLifecycleException
    {
		container.release(component);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#releaseAll(java.util.Map)
	 */
	public void releaseAll(Map components)
        throws ComponentLifecycleException
    {
		container.releaseAll(components);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#releaseAll(java.util.List)
	 */
	public void releaseAll(List components)
        throws ComponentLifecycleException
    {
		container.releaseAll(components);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#hasComponent(java.lang.String)
	 */
	public boolean hasComponent(String componentKey)
	{
		return container.hasComponent(componentKey);
	}

	/**
	 * @see org.codehaus.xfire.lifecycle.ServiceLocator#hasComponent(java.lang.String, java.lang.String)
	 */
	public boolean hasComponent(String role, String roleHint)
	{
		return container.hasComponent(role, roleHint);
	}
}
