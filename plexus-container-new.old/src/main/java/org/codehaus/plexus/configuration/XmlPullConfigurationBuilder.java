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
     * Likely number of nested configuration items. If more is
     * encountered the lists will grow automatically.
     */
    private static final int EXPECTED_DEPTH = 5;
    private final ArrayList elements = new ArrayList( EXPECTED_DEPTH );
    private final ArrayList values = new ArrayList( EXPECTED_DEPTH );
    /**
     * Contains true at index n if space in the configuration with
     * depth n is to be preserved.
     */
    private final BitSet preserveSpace = new BitSet();
    private Configuration configuration;

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
        clear();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput( reader );

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                final DefaultConfiguration configuration =
                    createConfiguration( rawName, getLocationString() );
                // depth of new configuration (not decrementing here, configuration
                // is to be added)
                final int depth = elements.size();
                boolean preserveSpace = false; // top level element trims space by default

                if ( depth > 0 )
                {
                    final DefaultConfiguration parent =
                        (DefaultConfiguration) elements.get( depth - 1 );
                    parent.addChild( configuration );
                    // inherits parent's space preservation policy
                    preserveSpace = this.preserveSpace.get( depth - 1 );
                }

                elements.add( configuration );
                values.add( new StringBuffer() );

                final int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    final String name = parser.getAttributeName( i );
                    final String value = parser.getAttributeValue( i );

                    if ( !name.equals( "xml:space" ) )
                    {
                        configuration.setAttribute( name, value );
                    }
                    else
                    {
                        preserveSpace = value.equals( "preserve" );
                    }
                }

                if ( preserveSpace )
                {
                    this.preserveSpace.set( depth );
                }
                else
                {
                    this.preserveSpace.clear( depth );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                // it is possible to play micro-optimization here by doing
                // manual trimming and thus preserve some precious bits
                // of memory, but it's really not important enough to justify
                // resulting code complexity
                final int depth = values.size() - 1;
                final StringBuffer valueBuffer = (StringBuffer) values.get( depth );
                valueBuffer.append( parser.getText() );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {

                final int depth = elements.size() - 1;
                final DefaultConfiguration finishedConfiguration =
                    (DefaultConfiguration) elements.remove( depth );
                final String accumulatedValue = ( values.remove( depth ) ).toString();

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
                    final String trimmedValue = accumulatedValue.trim();
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
     * Clears all data from this configuration handler.
     */
    public void clear()
    {
        elements.clear();
        values.clear();
    }

    /**
     * Create a new <code>DefaultConfiguration</code> with the specified
     * local name and location.
     *
     * @param localName a <code>String</code> value
     * @param location a <code>String</code> value
     * @return a <code>DefaultConfiguration</code> value
     */
    protected DefaultConfiguration createConfiguration( final String localName,
                                                        final String location )
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
