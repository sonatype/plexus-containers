package org.codehaus.plexus;

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

import junit.framework.TestCase;

/**
 * This is the start of the sketch which outlines some of the things
 * I would like to do with components during runtime.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DyanamicComponentKungFuTest
    extends TestCase
{
    /**
     * Component additions during container operation.
     *
     * 1. Add a component at runtime
     *    -> Additions could be made by specifying an URL which will be compatible with Wagon
     *       and specifically Maven's way of using Wagon.
     *
     * 2. Configure the dynamically added component
     *    -> We need to be able to deal with different flavours of components but we can focus
     *       on Plexus components to start with. But some components may have meta information
     *       and some may not like pico components. But one of the first flavours I want to
     *       support is phoenix components because I specifically need the FTP server.
     *
     * 3. Let the component perform its role
     *
     * 4. Suspend the component
     *    a) Define the criteria for which we can suspend a component
     *       -> When there are no client connections?
     *       -> Even when there are no connections and a client tries to obtain a connection what do we do?
     *       -> If we are in desperate need to suspend the component, say for urgent security requirement, and
     *          clients simply won't bugger off what do we do?
     *
     * 5. Reconfigure the component
     *
     * 6. Resume the component
     *
     * 7. Let the component perform its role
     *
     * 8. Release the component
     */
    public void testAdditionOfComponentDuringContainerOperation()
        throws Exception
    {
    }

    /**
     * Component replacement during container operation.
     *
     * This will force the design of a mechanism where the components communicate
     * with one another via a connector. In order for components to be replaced
     * dynamically the components cannot be directly coupled to one another.
     *
     * How to decide if a component is a suitable replacement given the versions
     * of the specifications of the component and any required components if the
     * component is a composite component.
     *
     * Definitely need to simulate the connection (a MockConnection) during
     * runtime to make sure that in the event something goes wrong the container
     * can just refuse to allow the component substitution. This shouldn't be trial
     * and error but until much field testing has occurred I'm sure there will be
     * instances where miscalculations happen simply due to lack of experience and
     * usage with dynamic component replacement.
     */
    public void testComponentReplacementDuringContainerOperation()
        throws Exception
    {

    }
}
