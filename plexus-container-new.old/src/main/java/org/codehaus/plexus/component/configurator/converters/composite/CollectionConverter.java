package org.codehaus.plexus.component.configurator.converters.composite;

import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfiguratorUtils;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class CollectionConverter extends AbstractCompositeConverter
{
    public boolean canConvert( Class type )
    {
        return Collection.class.isAssignableFrom( type );
    }

    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     ClassLoader classLoader,
                                     ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Collection retValue = null;

        Class implementation = getClassForImplementationHint( null, configuration, classLoader, componentDescriptor );

        if ( implementation != null )
        {
            retValue = ( Collection ) instantiateObject( implementation, componentDescriptor );
        }

        else
        {
            // we can have 2 cases here:
            //  - provided collection class which is not abstract
            //     like Vector, ArrayList, HashSet - so we will just instantantiate it
            // - we have an abtract class so we have to use default collection type
            int modifiers = type.getModifiers();

            if ( Modifier.isAbstract( modifiers ) )
            {
                retValue = getDefaultCollection( type );
            }
            else
            {
                try
                {
                    retValue = ( Collection ) type.newInstance();
                }
                catch ( Exception e )
                {
                    //@todo improve message
                    String msg = "Error configuring component: "
                            + componentDescriptor.getHumanReadableKey() + ":";

                    throw new ComponentConfigurationException( msg, e );
                }
            }
        }
        // now we have collection and we have to add some objects to it

        for ( int i = 0; i < configuration.getChildCount(); i++ )
        {
            PlexusConfiguration c = configuration.getChild( i );
            //Object o = null;

            String conifgEntry = c.getName();

            String componentClassname = componentDescriptor.getImplementation();

            String basePackage = componentClassname.substring( 0, componentClassname.lastIndexOf( "." ) );

            String name = StringUtils.capitalizeFirstLetter( ComponentConfiguratorUtils.fromXML( conifgEntry ) );

            String classname = basePackage + "." + name;

            Class childType = loadClass( classname, classLoader, componentDescriptor );

            childType = getClassForImplementationHint( childType, c, classLoader, componentDescriptor );

            CompositeConverter converter = converterLookup.lookupCompositeConverterForType( childType );

            Object object = converter.fromConfiguration( converterLookup, c, childType, classLoader, componentDescriptor );

            retValue.add( object );
        }

        return retValue;
    }

    protected Collection getDefaultCollection( Class collectionType )
    {
        Collection retValue = null;

        if ( List.class.isAssignableFrom( collectionType ) )
        {
            retValue = new ArrayList();
        }
        else if ( Set.class.isAssignableFrom( collectionType ) )
        {
            retValue = new HashSet();
        }

        return retValue;
    }

}
