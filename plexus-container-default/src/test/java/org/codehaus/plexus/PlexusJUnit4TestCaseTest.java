package org.codehaus.plexus;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class PlexusJUnit4TestCaseTest
{

    private static AtomicBoolean run = new AtomicBoolean( false );
    public static class MyTest
        extends PlexusJUnit4TestCase {

        @Test
        public void yeah(){
            run.set( true );
        }
    }

    @Test
    public void runMytest(){
        JUnitCore.runClasses(MyTest.class);
        assertTrue( run.get() );
    }
}