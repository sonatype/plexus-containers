package org.codehaus.plexus.component;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.xbean.recipe.MissingAccessorException;
import junit.framework.TestCase;

import java.util.List;

public class PlexusExceptionTest extends TestCase
{
    private static DefaultPlexusContainer container;
    private ComponentDescriptor<ExceptionalComponent> exceptionalDescriptor;
    private ComponentDescriptor<RequiresComponent> requiresDescriptor;

    private static Exception constructorException;
    private static Exception setterException;
    private static Exception startException;
    private static boolean lookupInSetter;
    private static boolean lookupInConstructor;

    protected void setUp() throws Exception
    {
        super.setUp();
        container = new DefaultPlexusContainer();

        //
        // ExceptionalComponent definition
        //
        exceptionalDescriptor = new ComponentDescriptor<ExceptionalComponent>(
            ExceptionalComponent.class,
            container.getContainerRealm() );
        exceptionalDescriptor.setRoleClass( ExceptionalComponent.class );
        PlexusConfiguration exceptionalConfiguration = new DefaultPlexusConfiguration(){};
        exceptionalConfiguration.setAttribute( "name", "exceptional bean" );
        exceptionalDescriptor.setConfiguration( exceptionalConfiguration );
        exceptionalDescriptor.setSource( "exceptionalDescriptor.xml" );

        container.addComponentDescriptor( exceptionalDescriptor );

        //
        // RequiresComponent definition
        //
        requiresDescriptor = new ComponentDescriptor<RequiresComponent>(
            RequiresComponent.class,
            container.getContainerRealm() );
        requiresDescriptor.setRoleClass( RequiresComponent.class );
        PlexusConfiguration requiresConfiguration = new DefaultPlexusConfiguration(){};
        requiresConfiguration.setAttribute( "name", "requires bean" );
        requiresDescriptor.setConfiguration( requiresConfiguration );
        requiresDescriptor.setSource( "requiresDescriptor.xml" );

        container.addComponentDescriptor( requiresDescriptor );

        constructorException = null;
        setterException = null;
        startException = null;
        lookupInConstructor = false;
        lookupInSetter = false;
    }

    public void testConstructorCheckedException() throws Exception {
        constructorException = new TestCheckedException( "constructor test" );

        assertLookupFailed( constructorException );
    }

    public void testConstructorRuntimeException() throws Exception {
        constructorException = new TestRuntimeException( "constructor test" );

        assertLookupFailed( constructorException );
    }

    public void testSetterCheckedException() throws Exception {
        setterException = new TestCheckedException( "setter test");

        assertLookupFailed( setterException );
    }

    public void testSetterRuntimeException() throws Exception {
        setterException = new TestRuntimeException( "setter test");

        assertLookupFailed( setterException );
    }

    public void testStartCheckedException() throws Exception {
        startException = new StartingException( "start test");

        assertLookupFailed( startException );
    }

    public void testStartRuntimeException() throws Exception {
        startException = new TestRuntimeException( "start test");

        assertLookupFailed( startException );
    }

    public void testMissingProperty() throws Exception {
        exceptionalDescriptor.getConfiguration().addChild( "unknown", "unknown" );

        Throwable cause = assertLookupFailed( null );
        assertTrue("cause should be an instance of MissingAccessorException", cause instanceof MissingAccessorException );
    }

    public void testMissingRequirementProperty() throws Exception {
        exceptionalDescriptor.addRequirement( new ComponentRequirement( "unknown", ExceptionalComponent.class.getName()) );

        Throwable cause = assertLookupFailed( null );
        assertTrue("cause should be an instance of MissingAccessorException", cause instanceof MissingAccessorException );
    }

    private Throwable assertLookupFailed( Exception expected )
    {
        try
        {
            container.lookup( ExceptionalComponent.class );
            fail("Expected ComponentLookupException");
            throw new AssertionError("Unreachable statement");
        }
        catch ( ComponentLookupException lookupException )
        {
            lookupException.printStackTrace(  );

            // verify cause is the same excption thrown from the component constructor
            Throwable cause = lookupException.getCause();
            assertNotNull( "ComponentLookupException.getCause() is null", cause );
            if ( expected != null )
            {
                assertSame( "cause should be same instance thrown from component", expected, cause );
            }

            // verify stack contains only the one component
            List<ComponentStackElement> stack = lookupException.getComponentStack();
            assertEquals( "Component stack", 1, stack.size() );
            ComponentDescriptor<?> failedDescriptor = stack.get( 0 ).getDescriptor();
            assertSame( "Failed component descriptor should be created component", exceptionalDescriptor, failedDescriptor );

            return lookupException.getCause();
        }
    }

    public void testNestedRequiresConstructorCheckedException() throws Exception {
        constructorException = new TestCheckedException( "constructor test" );
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( constructorException );
    }

    public void testNestedRequiresConstructorRuntimeException() throws Exception {
        constructorException = new TestRuntimeException( "constructor test" );
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( constructorException );
    }

    public void testNestedRequiresSetterCheckedException() throws Exception {
        setterException = new TestCheckedException( "setter test");
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( setterException );
    }

    public void testNestedRequiresSetterRuntimeException() throws Exception {
        setterException = new TestRuntimeException( "setter test");
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( setterException );
    }

    public void testNestedRequiresStartCheckedException() throws Exception {
        startException = new StartingException( "start test");
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( startException );
    }

    public void testNestedRequiresStartRuntimeException() throws Exception {
        startException = new TestRuntimeException( "start test");
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        assertNestedRequiresFailed( startException );
    }

    public void testNestedRequiresMissingProperty() throws Exception {
        exceptionalDescriptor.getConfiguration().addChild( "unknown", "unknown" );
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        Throwable cause = assertNestedRequiresFailed( null );
        assertTrue("cause should be an instance of MissingAccessorException", cause instanceof MissingAccessorException );
    }

    public void testNestedRequiresMissingRequirementProperty() throws Exception {
        exceptionalDescriptor.addRequirement( new ComponentRequirement( "unknown", ExceptionalComponent.class.getName()) );
        requiresDescriptor.addRequirement( new ComponentRequirement( "component", ExceptionalComponent.class.getName()) );

        Throwable cause = assertNestedRequiresFailed( null );
        assertTrue("cause should be an instance of MissingAccessorException", cause instanceof MissingAccessorException );
    }

    public void testNestedLookupInConstructor() throws Exception {
        constructorException = new TestCheckedException( "constructor test" );
        lookupInConstructor = true;

        assertNestedRequiresFailed( constructorException );
    }

    public void testNestedLookupInSetter() throws Exception {
        constructorException = new TestCheckedException( "constructor test" );
        lookupInSetter = true;

        assertNestedRequiresFailed( constructorException );
    }

    private Throwable assertNestedRequiresFailed( Exception expected )
    {
        try
        {
            container.lookup( RequiresComponent.class );
            fail("Expected ComponentLookupException");
            throw new AssertionError("Unreachable statement");
        }
        catch ( ComponentLookupException lookupException )
        {
            lookupException.printStackTrace(  );

            // verify cause is the same excption thrown from the component constructor
            Throwable cause = lookupException.getCause();
            assertNotNull( "ComponentLookupException.getCause() is null", cause );
            if ( expected != null )
            {
                assertSame( "cause should be same instance thrown from component", expected, cause );
            }

            // verify stack contains only the one component
            List<ComponentStackElement> stack = lookupException.getComponentStack();
            assertEquals( "Component stack", 2, stack.size() );
            ComponentDescriptor<?> failedDescriptor = stack.get( 0 ).getDescriptor();
            assertSame( "Failed component descriptor should be created component", exceptionalDescriptor, failedDescriptor );
            ComponentStackElement wrapperElement = stack.get( 1 );
            ComponentDescriptor<?> wrapperDescriptor = wrapperElement.getDescriptor();
            assertSame( "Wrapper component descriptor should be looked-up component", requiresDescriptor, wrapperDescriptor );
            if ( requiresDescriptor.getRequirements().size() > 0 )
            {
                assertSame( "Wrapper property", "component", wrapperElement.getProperty() );
            }

            return lookupException.getCause();
        }
    }

    public static class ExceptionalComponent implements Startable
    {
        private String myName;

        public ExceptionalComponent() throws Exception
        {
            if ( constructorException != null )
            {
                throw constructorException;
            }
        }

        public void setName( String name ) throws Exception
        {
            if ( setterException != null )
            {
                throw setterException;
            }
            this.myName = name;
        }

        public void start() throws StartingException
        {
            if ( startException instanceof RuntimeException )
            {
                throw (RuntimeException) startException;

            }
            if ( startException instanceof StartingException )
            {
                throw (StartingException) startException;

            }
            if ( startException != null )
            {
                throw new StartingException( "", startException );
            }
        }

        public void stop() throws StoppingException
        {
        }

        public String toString()
        {
            return myName;
        }
    }

    public static class RequiresComponent
    {
        private String myName;
        public ExceptionalComponent component;

        public RequiresComponent() throws Exception
        {
            if ( lookupInConstructor )
            {
                container.lookup( ExceptionalComponent.class );
            }
        }

        public void setName( String name ) throws Exception
        {
            if ( lookupInSetter )
            {
                container.lookup( ExceptionalComponent.class );
            }
            this.myName = name;
        }

        public String toString()
        {
            return myName;
        }
    }

    public static class TestRuntimeException extends RuntimeException
    {

        public TestRuntimeException( String message )
        {
            super( message );
        }

    }

    public static class TestCheckedException extends RuntimeException
    {

        public TestCheckedException( String message )
        {
            super( message );
        }

    }
}
