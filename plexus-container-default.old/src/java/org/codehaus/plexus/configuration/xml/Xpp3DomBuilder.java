package org.codehaus.plexus.configuration.xml;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.codehaus.plexus.configuration.xml.Xpp3Dom;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Xpp3DomBuilder
{
    public static Xpp3Dom build( Reader reader )
        throws Exception
    {
        List elements = new ArrayList();

        List values = new ArrayList();

        Xpp3Dom configuration = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser parser = factory.newPullParser();

        parser.setInput( reader );

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                Xpp3Dom childConfiguration = createConfiguration( rawName );

                int depth = elements.size();

                if ( depth > 0 )
                {
                    Xpp3Dom parent = (Xpp3Dom) elements.get( depth - 1 );

                    parent.addChild( childConfiguration );
                }

                elements.add( childConfiguration );

                values.add( new StringBuffer() );

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    childConfiguration.setAttribute( name, value );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                int depth = values.size() - 1;

                StringBuffer valueBuffer = (StringBuffer) values.get( depth );

                valueBuffer.append( parser.getText() );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                Xpp3Dom finishedConfiguration = (Xpp3Dom) elements.remove( depth );

                String accumulatedValue = ( values.remove( depth ) ).toString();

                if ( finishedConfiguration.getChildCount() == 0 )
                {
                    String finishedValue;

                    if ( 0 == accumulatedValue.length() )
                    {
                        finishedValue = null;
                    }
                    else
                    {
                        finishedValue = accumulatedValue;
                    }

                    finishedConfiguration.setValue( finishedValue );
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

    private static Xpp3Dom createConfiguration( String localName )
    {
        return new Xpp3Dom( localName );
    }
}