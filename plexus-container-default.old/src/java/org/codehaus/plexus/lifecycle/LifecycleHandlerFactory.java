package org.codehaus.plexus.lifecycle;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.codehaus.plexus.factory.AbstractPlexusFactory;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.component.repository.ComponentRepository;

public class LifecycleHandlerFactory
    extends AbstractPlexusFactory
{
    public static LifecycleHandlerHousing createLifecycleHandlerHousing( Configuration config,
                                                                         LoggerManager loggerManager,
                                                                         ClassLoader classLoader,
                                                                         Context context,
                                                                         ComponentRepository componentRepository )
        throws Exception
    {
        LifecycleHandlerHousing housing = new LifecycleHandlerHousing();

        String implementation = config.getChild( "implementation" ).getValue( null );

        if ( implementation == null )
        {
            throw new ConfigurationException( "No lifecycle implementation" );
        }

        String id = config.getChild( "id" ).getValue( null );

        if ( id == null )
        {
            throw new ConfigurationException( "No role specified for lifecycle handler" );
        }

        housing.setImplementation( implementation );

        housing.setId( id );

        housing.setName( config.getChild( "name" ).getValue( "" ) );

        LifecycleHandler lifecycleHandler = (LifecycleHandler) getInstance( implementation, classLoader );

        // Setup logging for the lifecycle handler. Not used by components
        lifecycleHandler.enableLogging( loggerManager.getLogger( "lifecycle-handler:" + id ) );

        Configuration[] a = config.getChild( "start-segment" ).getChildren( "phase" );

        for ( int i = 0; i < a.length; i++ )
        {
            lifecycleHandler.addBeginSegmentPhase( (Phase) getInstance( a[i].getAttribute( "implementation" ), classLoader ) );
        }

        Configuration[] b = config.getChild( "suspend-segment" ).getChildren( "phase" );

        for ( int i = 0; i < b.length; i++ )
        {
            lifecycleHandler.addSuspendSegmentPhase( (Phase) getInstance( b[i].getAttribute( "implementation" ), classLoader ) );
        }

        Configuration[] c = config.getChild( "resume-segment" ).getChildren( "phase" );

        for ( int i = 0; i < c.length; i++ )
        {
            lifecycleHandler.addResumeSegmentPhase( (Phase) getInstance( c[i].getAttribute( "implementation" ), classLoader ) );
        }

        Configuration[] d = config.getChild( "end-segment" ).getChildren( "phase" );

        for ( int i = 0; i < d.length; i++ )
        {
            lifecycleHandler.addEndSegmentPhase( (Phase) getInstance( d[i].getAttribute( "implementation" ), classLoader ) );
        }

        // Add some standard entities to the lifecycle handler. The lifecycle
        // handler may wish to use some of these entities to create new types
        // of entities for its lifecycle phases. For example the AvalonLifecycleHandler
        // uses the ServiceRepository and adapts it to create an Avalon ServiceManager.
        // The entities MUST be added before initialization of the lifecyclehandler.
        lifecycleHandler.addEntity( LifecycleHandler.LOGGER, loggerManager.getRootLogger() );

        lifecycleHandler.addEntity( LifecycleHandler.CONTEXT, context );

        lifecycleHandler.addEntity( LifecycleHandler.SERVICE_REPOSITORY, componentRepository );

        lifecycleHandler.initialize();

        //wrap the handler in an immutable wrapper. THis is so components can't modify it
        housing.setHandler( lifecycleHandler );

        return housing;
    }
}
