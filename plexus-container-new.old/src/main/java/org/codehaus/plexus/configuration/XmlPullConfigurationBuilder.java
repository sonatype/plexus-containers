package org.codehaus.plexus.configuration;

import org.apache.avalon.framework.configuration.Configuration;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class XmlPullConfigurationBuilder
{

    /**
     * Parse input from a character stream, building configuration items
     * from it. Close the stream when finished.
     *
     * @param reader Character stream to parse. Will be closed when parse
     * returns.
     * @return Configuration items.
     */
    public Configuration parse( Reader reader )
        throws Exception
    {
        ArrayList elements = new ArrayList();

        ArrayList values = new ArrayList();

        BitSet preserveSpace = new BitSet();

        Configuration configuration = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser parser = factory.newPullParser();

        parser.setInput( reader );

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                DefaultConfiguration childConfiguration = createConfiguration( rawName, getLocationString() );
                // depth of new childConfiguration (not decrementing here, childConfiguration
                // is to be added)
                int depth = elements.size();

                boolean childPreserveSpace = false; // top level element trims space by default

                if ( depth > 0 )
                {
                    DefaultConfiguration parent = (DefaultConfiguration) elements.get( depth - 1 );

                    parent.addChild( childConfiguration );

                    // inherits parent's space preservation policy
                    childPreserveSpace = preserveSpace.get( depth - 1 );
                }

                elements.add( childConfiguration );

                values.add( new StringBuffer() );

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    if ( !name.equals( "xml:space" ) )
                    {
                        childConfiguration.setAttribute( name, value );
                    }
                    else
                    {
                        childPreserveSpace = value.equals( "preserve" );
                    }
                }

                if ( childPreserveSpace )
                {
                    preserveSpace.set( depth );
                }
                else
                {
                    preserveSpace.clear( depth );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                // it is possible to play micro-optimization here by doing
                // manual trimming and thus preserve some precious bits
                // of memory, but it's really not important enough to justify
                // resulting code complexity
                int depth = values.size() - 1;

                StringBuffer valueBuffer = (StringBuffer) values.get( depth );

                valueBuffer.append( parser.getText() );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                DefaultConfiguration finishedConfiguration = (DefaultConfiguration) elements.remove( depth );

                String accumulatedValue = ( values.remove( depth ) ).toString();

                if ( finishedConfiguration.getChildren().length == 0 )
                {
                    // leaf node
                    String finishedValue;
                    if ( preserveSpace.get( depth ) )
                    {
                        finishedValue = accumulatedValue;
                    }
                    else if ( 0 == accumulatedValue.length() )
                    {
                        finishedValue = null;
                    }
                    else
                    {
                        finishedValue = accumulatedValue.trim();
                    }

                    finishedConfiguration.setValue( finishedValue );
                }
                else
                {
                    String trimmedValue = accumulatedValue.trim();

                    if ( trimmedValue.length() > 0 )
                    {
                        throw new Exception( "Not allowed to define mixed content in the "
                                             + "element " + finishedConfiguration.getName() + " at "
                                             + finishedConfiguration.getLocation() );
                    }
                }

                if ( 0 == depth )
                {
                    configuration = finishedConfiguration;
                }
            }

            eventType = parser.next();
        }

        reader.close();

        return configuration;
    }

    /**
     * Create a new <code>DefaultConfiguration</code> with the specified
     * local name and location.
     *
     * @param localName a <code>String</code> value
     * @param location a <code>String</code> value
     * @return a <code>DefaultConfiguration</code> value
     */
    protected DefaultConfiguration createConfiguration( String localName,
                                                        String location )
    {
        return new DefaultConfiguration( localName, location );
    }

    /**
     * Returns a string showing the current system ID, line number and column number.
     *
     * @return a <code>String</code> value
     */
    protected String getLocationString()
    {
        return "Unknown";
    }
}