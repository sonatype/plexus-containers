package org.codehaus.plexus.lifecycle.avalon.phase;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.ServiceA;
import org.codehaus.plexus.lifecycle.avalon.phase.AllPhaseService;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.DefaultComponentRepository;
import org.codehaus.plexus.configuration.DefaultConfiguration;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class PhaseTest
    extends PlexusTestCase
{
    ComponentDescriptor componentDescriptor;

    ComponentManager manager;

    AllPhaseService service;

    /**
     * @param testName
     */
    public PhaseTest( String testName )
    {
        super( testName );
    }

    public void setUp()
        throws Exception
    {
        super.setUp();

        setupComponentDescriptor();

        service = (AllPhaseService) lookup( ServiceA.ROLE );

        DefaultComponentRepository repo = (DefaultComponentRepository) getContainer().getComponentRepository();

        manager = repo.instantiateComponentManager( componentDescriptor );
    }

    public void tearDown()
        throws Exception
    {
        release( service );

        super.tearDown();
    }

    protected void setupComponentDescriptor()
    {
        componentDescriptor = new ComponentDescriptor();

        String role = "org.codehaus.plexus.ServiceA";

        String roleHint = "role-hint";

        String instantiation = "singleton";

        componentDescriptor.setRole( role );

        componentDescriptor.setRoleHint( roleHint );

        componentDescriptor.setInstantiationStrategy( instantiation );

        componentDescriptor.addRequirement( "foo" );

        componentDescriptor.setConfiguration( new DefaultConfiguration( "config" ) );
    }

    public void testContextualizePhase() throws Exception
    {
        ContextualizePhase phase = new ContextualizePhase();

        phase.execute( service, manager );

        assertTrue( service.contextualize );

        manager.getLifecycleHandler().getEntities().remove( "context" );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no  context available." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testServicePhase() throws Exception
    {
        ServicePhase phase = new ServicePhase();

        phase.execute( service, manager );

        assertTrue( service.service );

        manager.getLifecycleHandler().getEntities().remove( "component.manager" );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no component manager." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testLogEnablePhase() throws Exception
    {
        LogEnablePhase phase = new LogEnablePhase();

        phase.execute( service, manager );

        assertTrue( service.enableLogging );

        manager.getLifecycleHandler().getEntities().remove( "logger" );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no logger available." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testConfigurePhase() throws Exception
    {
        ConfigurePhase phase = new ConfigurePhase();

        phase.execute( service, manager );

        assertTrue( service.configure );

        manager.getComponentDescriptor().setConfiguration( null );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no configuration." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testDisposePhase() throws Exception
    {
        DisposePhase phase = new DisposePhase();

        phase.execute( service, manager );

        assertTrue( service.dispose );
    }

    public void testReconfigurePhase() throws Exception
    {
        ReconfigurePhase phase = new ReconfigurePhase();

        phase.execute( service, manager );

        assertTrue( service.reconfigure );

        manager.getComponentDescriptor().setConfiguration( null );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no configuration." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testRecontextualizePhase() throws Exception
    {
        RecontextualizePhase phase = new RecontextualizePhase();

        phase.execute( service, manager );

        assertTrue( service.recontextualize );
    }

    public void testRecontextualizePhaseWithoutContext() throws Exception
    {
        RecontextualizePhase phase = new RecontextualizePhase();

        phase.execute( service, manager );

        assertTrue( service.contextualize );

        manager.getLifecycleHandler().getEntities().remove( "context" );

        try
        {
            phase.execute( service, manager );

            fail( "Phase should fail with no  context available." );
        }
        catch ( IllegalArgumentException e )
        {
        }
    }

    public void testSuspendPhase() throws Exception
    {
        SuspendPhase phase = new SuspendPhase();

        phase.execute( "not-suspendable", manager );

        phase.execute( service, manager );

        assertTrue( service.suspend );
    }

    public void testResumePhase() throws Exception
    {
        ResumePhase phase = new ResumePhase();

        phase.execute( "not-resumable", manager );

        phase.execute( service, manager );

        assertTrue( service.resume );
    }
}
