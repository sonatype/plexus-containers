package org.codehaus.plexus.javadoc;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.plugin.javadoc.JavadocReport;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class JavadocReportTest
    extends AbstractMojoTestCase
{
    private static final String LINE_SEPARATOR = " ";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
        createTestRepo();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        // nop
    }

    /**
     * Create test repository in target directory.
     */
    private void createTestRepo()
    {
        File f = new File( getBasedir(), "target/local-repo/" );
        f.mkdirs();

        f = new File( getBasedir(), "target/remote-repo/" );
        f.mkdirs();
    }

    /**
     * Convenience method that reads the contents of the specified file object into a string with a
     * <code>space</code> as line separator.
     *
     * @see #LINE_SEPARATOR
     * @param file the file to be read
     * @return a String object that contains the contents of the file
     * @throws IOException if any
     */
    private static String readFile( File file )
        throws IOException
    {
        String str = "", strTmp = "";
        BufferedReader in = new BufferedReader( new FileReader( file ) );

        while ( ( strTmp = in.readLine() ) != null )
        {
            str = str + LINE_SEPARATOR + strTmp;
        }
        in.close();

        return str;
    }

    /**
     * Test the default javadoc renderer using the Maven plugin
     * <code>org.apache.maven.plugins:maven-javadoc-plugin:2.3</code>
     *
     * @throws Exception
     */
    public void testPlexusTaglets()
        throws Exception
    {
        File testPom = new File( getBasedir(), "src/test/resources/unit/plexus/plexus-plugin-config.xml" );
        PlexusConfiguration pluginConfiguration = extractPluginConfiguration( "maven-javadoc-plugin", testPom );
        JavadocReport mojo = (JavadocReport) lookupMojo( "org.apache.maven.plugins", "maven-javadoc-plugin", "2.4",
                                                         "javadoc", pluginConfiguration );
        // Don't know we need to specify that
        ArtifactRepository repository = new DefaultArtifactRepository( "central", "file://"
            + PlexusTestCase.getBasedir() + "/src/test/remote-repo", new DefaultRepositoryLayout() );

        setVariableValueToObject( mojo, "remoteRepositories", Collections.singletonList( repository ) );
        mojo.execute();

        File generatedFile = new File( getBasedir(), "target/test/unit/plexus/target/site/apidocs/plexus/test/App.html" );
        assertTrue( FileUtils.fileExists( generatedFile.getAbsolutePath() ) );

        String str = readFile( generatedFile );
        assertTrue( str.toLowerCase().indexOf(
                                               ("<DT><B>Plexus component:</B></DT><DD>"
                                                   + "<TABLE CELLPADDING=\"2\" CELLSPACING=\"0\"><TR><TD>" + "<DL>"
                                                   + "<DT><B>role:</B></DT>"
                                                   + "<DD>\"org.codehaus.plexus.test.App\"</DD>"
                                                   + "<DT><B>role-hint:</B></DT>" + "<DD>\"app\"</DD>"
                                                   + "</DL></TD></TR></TABLE></DD>" ).toLowerCase()) != -1 );

        assertTrue( str.toLowerCase().indexOf(
                                               ( "<DT><B>Plexus configuration:</B></DT><DD>"
                                                   + "<TABLE CELLPADDING=\"2\" CELLSPACING=\"0\"><TR><TD><DL>"
                                                   + "<DT><B>default-value:</B></DT>"
                                                   + "<DD>\"someone\"</DD></DL></TD></TR></TABLE></DD></DL>" )
                                                   .toLowerCase() ) != -1 );

        assertTrue( str.toLowerCase().indexOf(
                                               ( "<DT><B>Plexus requirement:</B></DT><DD>"
                                                   + "<TABLE CELLPADDING=\"2\" CELLSPACING=\"0\"><TR><TD><DL>"
                                                   + "<DT><B>role-hint:</B></DT><DD>\"foo\"</DD>"
                                                   + "</DL></TD></TR></TABLE></DD></DL>" ).toLowerCase() ) != -1 );

    }
}
