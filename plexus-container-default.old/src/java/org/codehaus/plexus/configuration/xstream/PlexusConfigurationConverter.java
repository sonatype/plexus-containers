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
        // When I create the configuration serializer I'll use that here.
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
