package org.codehaus.plexus.configuration.pipeline;

import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.configuration.ObjectBuilder;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;

import java.io.StringReader;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class PipelineBuilderTest
    extends TestCase
{
    public void testObjectBuilderWithSpecifiedImplementation()
        throws Exception
    {
        String configuration =
            "<pipeline>" +
            "  <valves>" +
            "    <valve implementation='org.codehaus.plexus.configuration.pipeline.FirstValve'/>" +
            "    <valve implementation='org.codehaus.plexus.configuration.pipeline.SecondValve'/>" +
            "  </valves>" +
            "</pipeline>";

        ObjectBuilder builder = new ObjectBuilder();

        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();

        Configuration c = cb.parse( new StringReader( configuration ) );

        Pipeline pipeline = (Pipeline) builder.build( c, Pipeline.class );

        Valve firstValve = pipeline.getValve( 0 );

        assertNotNull( firstValve );

        assertEquals( "first", firstValve.getId() );

        Valve secondValve = pipeline.getValve( 1 );

        assertNotNull( secondValve );

        assertEquals( "second", secondValve.getId() );
    }
}
