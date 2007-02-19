package org.codehaus.plexus.logging;

import junit.framework.TestCase;

/**
 * A quick test to make sure the NullLogger is working when we fail to configure our logging.
 *
 * @author Andrew Williams
 * @since 1.0-alpha-18
 * @version $Id$
 */
public class NullLoggerTest
    extends TestCase
{
    private SimpleLogEnabled testClass;

    public void setUp()
    {
        testClass = new SimpleLogEnabled();
    }

    public void testSingleNullLogger()
    {
        assertNotNull( testClass.getLogger() );
    }

    public void testMultiNullLogger()
    {
        AbstractLogEnabled enabled = new AnotherLogEnabled();

        assertFalse( testClass.getLogger().equals( enabled.getLogger() ) );
    }
}

class SimpleLogEnabled
    extends AbstractLogEnabled
{

}

class AnotherLogEnabled
    extends AbstractLogEnabled
{

}