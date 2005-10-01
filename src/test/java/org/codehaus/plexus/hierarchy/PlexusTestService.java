package org.codehaus.plexus.hierarchy;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Simple Avalon interface.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 */
public interface PlexusTestService
{
    /** Component role. */
    String ROLE = PlexusTestService.class.getName();

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
