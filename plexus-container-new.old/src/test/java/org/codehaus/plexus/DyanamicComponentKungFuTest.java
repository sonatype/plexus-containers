package org.codehaus.plexus;

/**
 * This is the start of the sketch which outlines some of the things
 * I would like to do with components during runtime.
 *
 * 1. Add a component at runtime
 * 2. Configure the dynamically added component
 * 3. Let the component perform its role
 * 4. Suspend the component
 *    a) Define the criteria for which we can suspend a component
 *       -> When there are no client connections?
 *       -> Even when there are no connections and a client tries to obtain a connection what do we do?
 *       -> If we are in desperate need to suspend the component, say for urgent security requirement, and
 *          clients simply won't bugger off what do we do?
 * 5. Reconfigure the component
 * 6. Resume the component
 * 7. Let the component perform its role
 * 8. Release the component
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DyanamicComponentKungFuTest
{
    public void testAdditionOfComponentDuringContainerRuntime()
        throws Exception
    {
    }
}
