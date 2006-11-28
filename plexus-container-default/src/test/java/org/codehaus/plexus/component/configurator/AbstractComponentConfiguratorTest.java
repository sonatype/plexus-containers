package org.codehaus.plexus.component.configurator;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfiguratorTest
    extends PlexusTestCase
{
    protected abstract String getRoleHint();

    protected ComponentConfigurator getComponentConfigurator()
        throws Exception
    {
        return (ComponentConfigurator) lookup( ComponentConfigurator.ROLE, getRoleHint() );
    }

    public void testComponentConfigurator()
        throws Exception
    {
        String xml = "<configuration>" + "  <int-value>0</int-value>" + "  <float-value>1</float-value>" +
            "  <long-value>2</long-value>" + "  <double-value>3</double-value>" + "  <string-value>foo</string-value>" +
            "  <important-things>" + "    <important-thing><name>jason</name></important-thing>" +
            "    <important-thing><name>tess</name></important-thing>" + "  </important-things>" + "  <configuration>" +
            "      <name>jason</name>" + "  </configuration>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ConfigurableComponent component = new ConfigurableComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertEquals( "check long value", 2L, component.getLongValue() );

        Assert.assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertEquals( "foo", component.getStringValue() );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( (ImportantThing) list.get( 1 ) ).getName() );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }

    public void testComponentConfiguratorWithAComponentThatProvidesSettersForConfiguration()
        throws Exception
    {
        String xml = "<configuration>" + "  <int-value>0</int-value>" + "  <float-value>1</float-value>" +
            "  <long-value>2</long-value>" + "  <double-value>3</double-value>" + "  <string-value>foo</string-value>" +
            "  <important-things>" + "    <important-thing><name>jason</name></important-thing>" +
            "    <important-thing><name>tess</name></important-thing>" + "  </important-things>" + "  <configuration>" +
            "      <name>jason</name>" + "  </configuration>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithSetters component = new ComponentWithSetters();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertTrue( component.intValueSet );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertTrue( component.floatValueSet );

        assertEquals( "check long value", 2L, component.getLongValue() );

        assertTrue( component.longValueSet );

        Assert.assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertTrue( component.doubleValueSet );

        assertEquals( "foo", component.getStringValue() );

        assertTrue( component.stringValueSet );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( (ImportantThing) list.get( 1 ) ).getName() );

        assertTrue( component.importantThingsValueSet );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );

        assertTrue( component.configurationValueSet );
    }

    public void testComponentConfigurationWhereFieldsToConfigureResideInTheSuperclass()
        throws Exception
    {
        String xml = "<configuration>" + "  <name>jason</name>" + "  <address>bollywood</address>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertEquals( "jason", component.getName() );

        assertEquals( "bollywood", component.getAddress() );
    }

    public void testComponentConfigurationWhereFieldsAreCollections()
        throws Exception
    {
        String xml = "<configuration>" + "  <vector>" + "    <important-thing>" + "       <name>life</name>" +
            "    </important-thing>" + "  </vector>" + "  <set>" + "    <important-thing>" +
            "       <name>life</name>" + "    </important-thing>" + "  </set>" +
            "   <list implementation=\"java.util.LinkedList\">" + "     <important-thing>" +
            "       <name>life</name>" + "    </important-thing>" + "  </list>" + "  <stringList>" +
            "    <something>abc</something>" + "    <somethingElse>def</somethingElse>" + "  </stringList>" +
            // TODO: implement List<int> etc..
            //  "<intList>" +
            //  "  <something>12</something>" +
            //  "  <somethingElse>34</somethingElse>" +
            //  "</intList>" +
            "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCollectionFields component = new ComponentWithCollectionFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        Vector vector = component.getVector();

        assertEquals( "life", ( (ImportantThing) vector.get( 0 ) ).getName() );

        assertEquals( 1, vector.size() );

        Set set = component.getSet();

        assertEquals( 1, set.size() );

        Object[] setContents = set.toArray();

        assertEquals( "life", ( (ImportantThing) setContents[0] ).getName() );

        List list = component.getList();

        assertEquals( list.getClass(), LinkedList.class );

        assertEquals( "life", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( 1, list.size() );

        List stringList = component.getStringList();

        assertEquals( "abc", (String) stringList.get( 0 ) );

        assertEquals( "def", (String) stringList.get( 1 ) );

        assertEquals( 2, stringList.size() );
    }

    public void testComponentConfigurationWhereFieldsAreArrays()
        throws Exception
    {
        String xml = "<configuration>" + "  <stringArray>" + "    <first-string>value1</first-string>" +
            "    <second-string>value2</second-string>" + "  </stringArray>" + "  <integerArray>" +
            "    <firstInt>42</firstInt>" + "    <secondInt>69</secondInt>" + "  </integerArray>" +
            "  <importantThingArray>" + "    <importantThing><name>Hello</name></importantThing>" +
            "    <importantThing><name>World!</name></importantThing>" + "  </importantThingArray>" +
            "  <objectArray>" + "    <java.lang.String>some string</java.lang.String>" +
            "    <importantThing><name>something important</name></importantThing>" +
            "    <whatever implementation='java.lang.Integer'>303</whatever>" + "  </objectArray>" + "  <urlArray>" +
            "    <url>http://foo.com/bar</url>" + "    <url>file://localhost/c:/windows</url>" + "  </urlArray>" +
            "  <fileArray>" + "    <file>c:/windows</file>" + "    <file>/usr/local/bin/foo.sh</file>" +
            "  </fileArray>" + "  <classArray>" + "    <class>java.lang.String</class>" +
            "    <class>org.codehaus.plexus.component.configurator.ComponentWithArrayFields</class>" +
            "  </classArray>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithArrayFields component = new ComponentWithArrayFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        String[] stringArray = component.getStringArray();

        assertEquals( 2, stringArray.length );

        assertEquals( "value1", stringArray[0] );

        assertEquals( "value2", stringArray[1] );

        Integer[] integerArray = component.getIntegerArray();

        assertEquals( 2, integerArray.length );

        assertEquals( new Integer( 42 ), integerArray[0] );

        assertEquals( new Integer( 69 ), integerArray[1] );

        ImportantThing[] importantThingArray = component.getImportantThingArray();

        assertEquals( 2, importantThingArray.length );

        assertEquals( "Hello", importantThingArray[0].getName() );

        assertEquals( "World!", importantThingArray[1].getName() );

        Object[] objectArray = component.getObjectArray();

        assertEquals( 3, objectArray.length );

        assertEquals( "some string", objectArray[0] );

        assertEquals( "something important", ( (ImportantThing) objectArray[1] ).getName() );

        assertEquals( new Integer( 303 ), objectArray[2] );

        URL[] urls = component.getUrlArray();

        assertEquals( new URL( "http://foo.com/bar" ), urls[0] );

        assertEquals( new URL( "file://localhost/c:/windows" ), urls[1] );

        File[] files = component.getFileArray();

        assertEquals( new File( "c:/windows" ), files[0] );

        assertEquals( new File( "/usr/local/bin/foo.sh" ), files[1] );

        Class[] classes = component.getClassArray();

        assertEquals( String.class, classes[0] );

        assertEquals( component.getClass(), classes[1] );
    }

    public void testComponentConfigurationWithCompositeFields()
        throws Exception
    {

        String xml = "<configuration>" +
            "  <thing implementation=\"org.codehaus.plexus.component.configurator.ImportantThing\">" +
            "     <name>I am not abstract!</name>" + "  </thing>" + "  <importantThing>" +
            "     <name>I am not abstract either!</name>" + "  </importantThing>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCompositeFields component = new ComponentWithCompositeFields();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        assertNotNull( component.getThing() );

        assertEquals( "I am not abstract!", component.getThing().getName() );

        assertEquals( "I am not abstract either!", component.getImportantThing().getName() );
    }

    public void testInvalidComponentConfiguration()
        throws Exception
    {

        String xml = "<configuration><goodStartElement>theName</badStopElement></configuration>";

        try
        {
            PlexusTools.buildConfiguration( "<Test-Invalid>", new StringReader( xml ) );

            fail( "Should have caused an error because of the invalid XML." );
        }
        catch ( PlexusConfigurationException e )
        {
            // should catch this...
            //TODO Don't spew this out into the system.out capture it somewhere. It's very distracting in the test output.
            //System.out.println( "Error Message:\n\n" + e.getLocalizedMessage() + "\n\n" );
            //System.err.println( "Error with stacktrace:\n\n" );
            //e.printStackTrace();
            //System.err.println( "\n\n" );
        }
        catch ( Exception e )
        {
            fail( "Should have caught the invalid plexus configuration exception." );
        }

    }

    public void testComponentConfigurationWithPropertiesFields()
        throws Exception
    {

        String xml = "<configuration>" + "  <someProperties>" + "     <property>" + "        <name>firstname</name>" +
            "        <value>michal</value>" + "     </property>" + "     <property>" + "        <name>lastname</name>" +
            "        <value>maczka</value>" + "     </property>" + "  </someProperties>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithPropertiesField component = new ComponentWithPropertiesField();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        Properties properties = component.getSomeProperties();

        assertNotNull( properties );

        assertEquals( "michal", properties.get( "firstname" ) );

        assertEquals( "maczka", properties.get( "lastname" ) );

    }

    public void testComponentConfigurationWithMapField()
        throws Exception
    {
        String xml = "<configuration>" + "  <map>" + "     <firstName>Kenney</firstName>" +
            "     <lastName>Westerhof</lastName>" + "  </map>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithMapField component = new ComponentWithMapField();

        ComponentConfigurator cc = getComponentConfigurator();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        cc.configureComponent( component, configuration, realm );

        Map map = component.getMap();

        assertNotNull( map );

        assertEquals( "Kenney", map.get( "firstName" ) );

        assertEquals( "Westerhof", map.get( "lastName" ) );

    }

}
