package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class PlexusConfigurationConverter
    implements Converter
{
    public boolean canConvert( Class type )
    {
        return PlexusConfiguration.class.isAssignableFrom( type );
    }

    public void toXML( ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup )
    {
        PlexusConfiguration configuration = (PlexusConfiguration) objectGraph.get();

        try
        {
            writeConfiguration( configuration, xmlWriter );
        }
        catch ( Exception e )
        {
            // do nothing.
        }
    }

    private void writeConfiguration( PlexusConfiguration configuration, XMLWriter xmlWriter )
        throws Exception
    {
        xmlWriter.startElement( configuration.getName() );

        String[] attributeNames = configuration.getAttributeNames();

        for ( int i = 0; i < attributeNames.length; i++ )
        {
            xmlWriter.addAttribute( attributeNames[i], configuration.getAttribute( attributeNames[i] ) );
        }

        int childCount = configuration.getChildCount();

        if ( childCount > 0 )
        {
            PlexusConfiguration[] configurations = configuration.getChildren();

            for ( int i = 0; i < childCount; i++ )
            {
                writeConfiguration( configurations[i], xmlWriter );
            }
        }
        else
        {
            xmlWriter.writeText( configuration.getValue() );
        }

        xmlWriter.endElement();
    }

    public void fromXML( ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType )
    {
        PlexusConfigurationReader reader = (PlexusConfigurationReader) xmlReader;

        PlexusConfiguration configuration = (PlexusConfiguration) reader.peek();

        while ( xmlReader.nextChild() )
        {
            xmlReader.pop();
        }

        objectGraph.set( configuration );
    }

}
