package org.codehaus.plexus.configuration;

import junit.framework.TestCase;
import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.apache.avalon.framework.configuration.Configuration;

import java.io.StringReader;
import java.util.List;

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
    private String configuration =
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

    public void testMessageBuilder()
        throws Exception
    {
        ObjectBuilder builder = new ObjectBuilder();

        XmlPullConfigurationBuilder cb = new XmlPullConfigurationBuilder();

        Configuration c = cb.parse( new StringReader( configuration ) );

        Message message = (Message) builder.build( c, Message.class );

        messageValueTest( message );
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
}
