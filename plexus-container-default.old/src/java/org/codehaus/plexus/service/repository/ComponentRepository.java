package org.codehaus.plexus.service.repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.lifecycle.LifecycleHandler;
import org.codehaus.plexus.lifecycle.UndefinedLifecycleHandlerException;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * Like the avalon service manager. Central point to get the components from.
 *
 *
 */
public interface ComponentRepository {
	void configure(
		Configuration defaultConfiguration,
		Configuration configuration);

	void contextualize(Context context);
	/**
	 * Initialize this repository
	 * @throws Exception
	 */
	void initialize() throws Exception;
	
	/**
	 * Lookup the component with the given role
	 * 
	 * @param role
	 * @return
	 * @throws ServiceException if no component with the given role exists, or there was an
	 * error taking the component through a lifecycle
	 */
	Object lookup(String role) throws ServiceException;

	Object lookup(String role, String id) throws ServiceException;

	/**
	 * Test if this repository manages the component with the given role
	 * 
	 * @param role
	 * @return
	 */
	boolean hasService(String role);

	/**
		 * Test if this repository manages the component with the given role
		 * and id
		 * 
		 * @param role
		 * @return
		 */
	boolean hasService(String role, String id);

	void release(Object service);

	/**
	 * Dispose of this Repository
	 *
	 */
	void dispose();

	void setPlexusContainer(PlexusContainer container);

	// Information

	/**
	 * Return the number of configured components
	 */
	int configuredComponents();

	/**
	 * Return the number of instantiated components
	 * @return
	 */
	int instantiatedComponents();

	ClassLoader getClassLoader();

	/** Set this repositories logger */
	void enableLogging(Logger logger);
	
	/** Set the logManager to be used for components */
	void setComponentLogManager(LoggerManager logManager);
	/**
	 * Start the lifecycle for the component in this housing
	 * 
	 * @param housing
	 */
	//void startComponentLifecycle(ComponentHousing housing);
	
	/**
	 * Return the lifecycle handler with the given id. Throws exception if no lifecycle
	 * handler with the given id exists. 
	 * 
	 * <p>Note: it is recommended the returned handler is immutable</p>
	 * 
	 * @param id
	 * @return
	 */
	LifecycleHandler getLifecycleHandler(String id)  throws UndefinedLifecycleHandlerException;
	
	/**
	 * Return the default lifecycle handler. This is the handler used for components 
	 * which don't specify a handler.
	 * 
	 * <p>Note: it is recommended the returned handler is immutable</p>
	 * 
	 * @return
	 */
	LifecycleHandler getDefaultLifecycleHandler();
}
