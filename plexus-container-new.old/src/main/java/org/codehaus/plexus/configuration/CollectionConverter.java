package org.codehaus.plexus.configuration;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.objecttree.ObjectTree;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.Collection;
import java.util.Iterator;

public class CollectionConverter extends AbstractCollectionConverter
{

    public CollectionConverter( ClassMapper classMapper )
    {
        super( classMapper );
    }

    public boolean canConvert( Class type )
    {
        return Collection.class.isAssignableFrom( type );
    }

    protected Object readItem( XMLReader xmlReader, ObjectTree objectGraph, ConverterLookup converterLookup )
    {
        String implementation = xmlReader.attribute( "implementation" );

        ObjectTree itemWriter = null;

        Class type = null;

        if ( implementation != null )
        {
            try
            {
                type = Class.forName( implementation );

                Object o = type.newInstance();

                itemWriter = objectGraph.newStack( o );
            }
            catch ( Exception e )
            {
                throw new ConversionException( "Cannot instantiate specified implementation: " + implementation );
            }
        }
        else
        {
            type = classMapper.lookupType( xmlReader.name() );

            itemWriter = objectGraph.newStack( type );
        }


        Converter converter = converterLookup.lookupConverterForType( type );

        converter.fromXML( itemWriter, xmlReader, converterLookup, type );

        return itemWriter.get();
    }

    public void toXML( ObjectTree objectGraph, XMLWriter xmlWriter, ConverterLookup converterLookup )
    {
        Collection collection = (Collection) objectGraph.get();
        for ( Iterator iterator = collection.iterator(); iterator.hasNext(); )
        {
            Object item = iterator.next();
            writeItem( item, xmlWriter, converterLookup, objectGraph );
        }
    }

    public void fromXML( ObjectTree objectGraph, XMLReader xmlReader, ConverterLookup converterLookup, Class requiredType )
    {
        Collection collection = (Collection) createCollection( requiredType );

        while ( xmlReader.nextChild() )
        {
            Object item = readItem( xmlReader, objectGraph, converterLookup );
            collection.add( item );
            xmlReader.pop();
        }
        objectGraph.set( collection );
    }

}
