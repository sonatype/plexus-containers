package org.codehaus.plexus.component.configurator.converters.lookup;

import org.codehaus.plexus.component.configurator.converters.composite.CompositeConverter;
import org.codehaus.plexus.component.configurator.converters.basic.Converter;


public interface ConverterLookup
{
    void registerCompositeConverter( CompositeConverter converter );

    void registerBasicConverter( Converter converter );    

    CompositeConverter lookupCompositeConverterForType( Class type );

    Converter lookupBasicConverterForType( Class type );
}
