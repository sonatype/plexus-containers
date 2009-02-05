package org.codehaus.plexus.component;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import junit.framework.TestCase;

import java.util.List;

public class PlexusExceptionTest extends TestCase
{
    private DefaultPlexusContainer container;
    private ComponentDescriptor<Component> descriptor;

    private static Exception constructorException;
    private static Exception setterException;
    private static Exception startException;
    private static Exception stopException;

    protected void setUp() throws Exception
    {
        super.setUp();
        container = new DefaultPlexusContainer();

        descriptor = new ComponentDescriptor<Component>(
            Component.class,
            container.getContainerRealm() );
        descriptor.setRoleClass( Component.class );
        PlexusConfiguration configuration = new DefaultPlexusConfiguration(){};
        configuration.setAttribute( "name", "testBean" );
        descriptor.setConfiguration( configuration );

        container.addComponentDescriptor( descriptor );

        constructorException = null;
        setterException = null;
        startException = null;
        stopException = null;
    }

    public void testConstructorCheckedException() throws Exception {
        constructorException = new TestCheckedException( "constructor test" );

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( constructorException, e );
        }
    }

    public void testConstructorRuntimeException() throws Exception {
        constructorException = new TestRuntimeException( "constructor test" );

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( constructorException, e );
        }
    }

    public void testSetterCheckedException() throws Exception {
        setterException = new TestCheckedException( "setter test");

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( setterException, e );
        }
    }

    public void testSetterRuntimeException() throws Exception {
        setterException = new TestRuntimeException( "setter test");

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( setterException, e );
        }
    }

    public void testStartCheckedException() throws Exception {
        startException = new StartingException( "start test");

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( startException, e );
        }
    }

    public void testStartRuntimeException() throws Exception {
        startException = new TestRuntimeException( "start test");

        try
        {
            container.lookup( Component.class );
            fail("Expected ComponentLookupException");
        }
        catch ( ComponentLookupException e )
        {
            assertValidComponentLookupException( startException, e );
        }
    }

    private void assertValidComponentLookupException( Exception expected, ComponentLookupException lookupException )
    {
        // verify cause is the same excption thrown from the component constructor
        Throwable cause = lookupException.getCause();
        assertNotNull( "ComponentLookupException.getCause() is null", cause );
        assertSame( "cause should be same instance thrown from component", expected, cause );

        // verify stack contains only the one component
        List<ComponentDescriptor<?>> stack = lookupException.getComponentStack();
        assertEquals( "Component stack", 1, stack.size() );
        ComponentDescriptor<?> failedDescriptor = stack.get( 0 );
        assertSame( "Failed component descriptor should be created component", descriptor, failedDescriptor );
    }

    public static class Component implements Startable
    {
        private String myName;

        public Component() throws Exception
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
            if ( stopException instanceof RuntimeException )
            {
                throw (RuntimeException) stopException;
            }
            if ( stopException instanceof StoppingException )
            {
                throw (StoppingException) stopException;

            }
            if ( stopException != null )
            {
                throw new StoppingException( "", stopException );
            }
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
