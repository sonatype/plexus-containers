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

        Message m = (Message) builder.build( c, Message.class );

        assertEquals( "papinet", m.getGroupId() );

        assertEquals( "Papinet", m.getGroupName() );

        assertEquals( "2.1", m.getGroupVersion() );

        assertEquals( "invoice", m.getId() );

        assertEquals( "Invoice", m.getName() );

        assertEquals( "org.foo.Bar", m.getClassName() );

        assertEquals( "unique-id-expression", m.getUniqueIdExpression() );

        assertEquals( "recipient-id-expression", m.getRecipientIdExpression() );

        Summary summary = m.getSummary();

        assertEquals( "invoice", summary.getId() );

        assertEquals( "Invoice", summary.getTitle() );

        List elements = summary.getElements();

        assertNotNull( elements );

        Element e = (Element) elements.get( 0 );

        assertNotNull( e );

        assertEquals( "invoice.id.label", e.getHeaderKey() );

        assertEquals( "expression", e.getExpression() );

        Message.InnerClass inner = m.getInnerClass();

        assertNotNull( inner );

        assertEquals( "inner-class-id", inner.getId() );
    }
}
