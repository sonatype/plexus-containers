package org.codehaus.plexus.hierarchy;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Simple Avalon interface.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public interface TestService
{
    /** Component role. */
    String ROLE = TestService.class.getName();

    String getPlexusName();

    String getKnownValue();

    /**
     * Get the known value contained in the TestService implementation
     * provided by the plexus with the given id.
     *
     * @param id Id of the Plexus instance to look-up.
     * @return Result of <code>getKnownValue</code> on the TestService in that
     * plexus.
     * @throws org.codehaus.plexus.component.repository.exception.ComponentLookupException If a plexus with the given id could not
     * be found. This exception would normally be thrown by the
     * <code>service</code> method, but it is delayed until this point
     * for the test case.
     */
    String getSiblingKnownValue( String id ) throws ComponentLookupException;
}
