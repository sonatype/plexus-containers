package org.codehaus.plexus.configuration.xstream;

import junit.framework.TestCase;

import java.io.StringReader;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.configuration.xstream.Element;
import org.codehaus.plexus.configuration.xstream.Message;
import org.codehaus.plexus.configuration.xstream.ObjectBuilder;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.DefaultConfiguration;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ObjectBuilderTest
    extends TestCase
{
    public void testMessageBuilder()
        throws Exception
    {
        String configuration =
            "<message>" +
            "  <group-id>papinet</group-id>" +
            "  <group-name>Papinet</group-name>" +
            "  <group-version>2.1</group-version> " +
            "  <id>invoice</id> " +
            "  <name>Invoice</name>" +
            "  <class-name>org.foo.Bar</class-name>" +
            "  <unique-id-expression>unique-id-expression</unique-id-expression>" +
            "  <recipient-id-expression>recipient-id-expression</recipient-id-expression>" +
            "  <intake-id>directory-intake</intake-id>" +
            "  <outtake-id>directory-outtake</outtake-id>" +
            "  <view-id>invoice</view-id>" +
            "  <summary>" +
            "    <id>invoice</id>" +
            "    <title>Invoice</title>" +
            "    <collection>collection</collection>" +
            "    <key-field>key-field</key-field>" +
            "    <elements>" +
            "      <element>" +
            "        <header-key>invoice.id.label</header-key>" +
            "        <expression>expression</expression>" +
            "      </element>" +
            "    </elements>" +
            "  </summary>" +
            "  <inner-class>" +
            "    <id>inner-class-id</id>" +
            "  </inner-class>" +
            "</message>";

        ObjectBuilder builder = new ObjectBuilder();

        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();

        PlexusConfiguration c = cb.parse( new StringReader( configuration ) );

        Message message = (Message) builder.build( c, Message.class );

        messageValueTest( message );

        DefaultConfiguration cc = builder.write( message );

        assertNotNull( cc );

        Message message1 = (Message) builder.build( cc, Message.class );

        assertNotNull( message1 );

        messageValueTest( message1 );
    }

    public void messageValueTest( Message message )
        throws Exception
    {
        assertEquals( "papinet", message.getGroupId() );

        assertEquals( "Papinet", message.getGroupName() );

        assertEquals( "2.1", message.getGroupVersion() );

        assertEquals( "invoice", message.getId() );

        assertEquals( "Invoice", message.getName() );

        assertEquals( "org.foo.Bar", message.getClassName() );

        assertEquals( "unique-id-expression", message.getUniqueIdExpression() );

        assertEquals( "recipient-id-expression", message.getRecipientIdExpression() );

        Summary summary = message.getSummary();

        assertEquals( "invoice", summary.getId() );

        assertEquals( "Invoice", summary.getTitle() );

        List elements = summary.getElements();

        assertNotNull( elements );

        Element e = (Element) elements.get( 0 );

        assertNotNull( e );

        assertEquals( "invoice.id.label", e.getHeaderKey() );

        assertEquals( "expression", e.getExpression() );

        Message.InnerClass inner = message.getInnerClass();

        assertNotNull( inner );

        assertEquals( "inner-class-id", inner.getId() );
    }

    public void testReadingObjectWithEmbeddedConfiguration()
        throws Exception
    {
        String configuration =
            "<person>" +
            "  <id>jvz</id>" +
            "  <name>jason</name>" +
            "  <configuration>" +
            "    <foo>bar</foo>" +
            "    <handlers>" +
            "      <one>1</one>" +
            "      <two>2</two>" +
            "    </handlers>" +
            "  </configuration>" +
            "  <occupation>muckraker</occupation>" +
            "</person>";

        ObjectBuilder builder = new ObjectBuilder();

        Person person = (Person) builder.build( new StringReader( configuration ), Person.class );

        assertNotNull( person );

        assertEquals( "jvz", person.getId() );

        assertEquals( "jason", person.getName() );

        Configuration c = person.getConfiguration();

        assertNotNull( c );

        assertEquals( "bar", c.getChild( "foo" ).getValue() );

        assertEquals( 1, c.getChild( "handlers").getChild( "one" ).getValueAsInteger() );

        assertEquals( 2, c.getChild( "handlers").getChild( "two" ).getValueAsInteger() );

        assertEquals( "muckraker", person.getOccupation() );
    }
}
