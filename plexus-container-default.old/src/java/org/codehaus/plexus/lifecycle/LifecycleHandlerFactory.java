package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.service.repository.ComponentRepository;

public class LifecycleHandlerFactory
    extends AbstractPlexusFactory
{
   /* public static LifecycleHandler create( Configuration defaultConfiguration,
                                           Configuration configuration,
                                           LoggerManager loggerManager,
                                           ClassLoader classLoader,
                                           Context context,
                                           ComponentRepository componentRepository )
        throws Exception
    {
        String implementation;
        Configuration c;

        if ( configuration.getChild( "lifecycle-handler", false ) != null )
        {
            c = configuration.getChild( "lifecycle-handler" );
            implementation = c.getChild( "implementation" ).getValue();
        }
        else
        {
            c = defaultConfiguration.getChild( "lifecycle-handler" );
            implementation = c.getChild( "implementation" ).getValue();
        }

        LifecycleHandler lh = (LifecycleHandler) getInstance( implementation, classLoader );

        // Setup logging
        lh.enableLogging( loggerManager.getLogger( "lifecycle-handler" ) );

        Configuration[] a = c.getChild( "start-segment" ).getChildren( "phase" );
        for ( int i = 0; i < a.length; i++ )
        {
            lh.addBeginSegmentPhase(
                (Phase) getInstance( a[i].getAttribute( "implementation" ), classLoader ) );
        }

        Configuration[] b = c.getChild( "end-segment" ).getChildren( "phase" );
        for ( int i = 0; i < b.length; i++ )
        {
            lh.addEndSegmentPhase(
                (Phase) getInstance( b[i].getAttribute( "implementation" ), classLoader ) );
        }

        // Add some standard entities to the lifecycle handler. The lifecycle
        // handler may wish to use some of these entities to create new types
        // of entities for its lifecycle phases. For example the AvalonLifecycleHandler
        // uses the ServiceRepository and adapts it to create an Avalon ServiceManager.
        // The entities MUST be added before initialization of the lifecyclehandler.
        lh.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );
        lh.addEntity( LifecycleHandler.CONTEXT, context );
        lh.addEntity( LifecycleHandler.SERVICE_REPOSITORY, componentRepository );

        // Initialize the lifecycle handler before returning the instance.
        lh.initialize();

        return lh;
    }*/
    
    /**
     * Return a new  a LifecycleHandlerHousing, with an instantiated and initialized 
     * Lifecyclehandler 
     * 
     * @param config
     * @param loggerManager
     * @param classLoader
     * @param context
     * @param componentRepository
     * @return
     * @throws Exception
     */
	public static LifecycleHandlerHousing createLifecycleHandlerHousing( Configuration config,
										   LoggerManager loggerManager,
										   ClassLoader classLoader,
										   Context context,
										   ComponentRepository componentRepository )
		throws Exception
	{
		LifecycleHandlerHousing housing = new LifecycleHandlerHousing();
			
		String implementation = config.getChild( "implementation" ).getValue( null );
		if( implementation == null)
		{
			throw new ConfigurationException("No lifecycle implementation");
		}
		String id = config.getChild("id").getValue(null);
		if( id == null )
		{
			throw new ConfigurationException("No role specified for lifecycle handler");
		}
		
		housing.setImplementation(implementation);
		housing.setId(id);
		housing.setConfiguration( config );
		LifecycleHandler lh = (LifecycleHandler) getInstance( implementation, classLoader );

		// Setup logging for the lifecycle handler. Not used by components
		lh.enableLogging( loggerManager.getLogger( "lifecycle-handler:" + id ) );

		Configuration[] a = config.getChild( "start-segment" ).getChildren( "phase" );
		for ( int i = 0; i < a.length; i++ )
		{
			lh.addBeginSegmentPhase(
				(Phase) getInstance( a[i].getAttribute( "implementation" ), classLoader ) );
		}

		Configuration[] b = config.getChild( "end-segment" ).getChildren( "phase" );
		for ( int i = 0; i < b.length; i++ )
		{
			lh.addEndSegmentPhase(
				(Phase) getInstance( b[i].getAttribute( "implementation" ), classLoader ) );
		}

		// Add some standard entities to the lifecycle handler. The lifecycle
		// handler may wish to use some of these entities to create new types
		// of entities for its lifecycle phases. For example the AvalonLifecycleHandler
		// uses the ServiceRepository and adapts it to create an Avalon ServiceManager.
		// The entities MUST be added before initialization of the lifecyclehandler.
		lh.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );
		lh.addEntity( LifecycleHandler.CONTEXT, context );
		lh.addEntity( LifecycleHandler.SERVICE_REPOSITORY, componentRepository );

		// Initialize the lifecycle handler before returning the instance.
		lh.configure(config.getChild("configuration"));
		lh.initialize();
		
		//wrap the handler in an immutable wrapper. THis is so components can't modify it
		housing.setHandler(lh);
		
		return housing;
	}
}
