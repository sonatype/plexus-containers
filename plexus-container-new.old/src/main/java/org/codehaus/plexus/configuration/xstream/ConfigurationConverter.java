package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.configuration.PlexusConfiguration;

public class ConfigurationConverter
    implements Converter
{
    public boolean canConvert( Class type )
    {
        return Configuration.class.isAssignableFrom( type );
    }

    public void toXML( ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup )
    {
        // When I create the configuration serializer I'll use that here.
    }

    public void fromXML( ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType )
    {
        ConfigurationReader reader = (ConfigurationReader) xmlReader;

        PlexusConfiguration configuration = (PlexusConfiguration) reader.peek();

        while ( xmlReader.nextChild() )
        {
            xmlReader.pop();
        }

        objectGraph.set( configuration );
    }

}
