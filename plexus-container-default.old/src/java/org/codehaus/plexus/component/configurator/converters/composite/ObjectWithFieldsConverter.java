package org.codehaus.plexus.component.configurator.converters.composite;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.configurator.ComponentConfiguratorUtils;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.basic.Converter;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.ReflectionUtils;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.lang.reflect.Field;


public class ObjectWithFieldsConverter extends AbstractCompositeConverter
{
    public boolean canConvert( Class type )
    {
        boolean retValue = true;

        if ( Dictionary.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }

        else if ( Map.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }
        else if ( Collection.class.isAssignableFrom( type ) )
        {
            retValue = false;
        }

        return retValue;
    }

    public Object fromConfiguration( ConverterLookup converterLookup,
                                     PlexusConfiguration configuration,
                                     Class type,
                                     ClassLoader classLoader,
                                     ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        // it is a "composite"
        Class implementation = getClassForImplementationHint( type,
                configuration,
                classLoader,
                componentDescriptor );

        Object retValue = instantiateObject( implementation, componentDescriptor );

        processConfiguration( converterLookup, retValue, classLoader, configuration, componentDescriptor );

        return retValue;

    }


    public void processConfiguration( ConverterLookup converterLookup,
                                      Object object,
                                      ClassLoader classLoader,
                                      PlexusConfiguration configuration,
                                      ComponentDescriptor componentDescriptor )
            throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration childConfiguration = configuration.getChild( i );

            String elementName = childConfiguration.getName();

            String fieldName = ComponentConfiguratorUtils.fromXML( elementName );

            Field field = getFieldByName( fieldName, object, componentDescriptor );

            Class fieldType = field.getType();

            // if configuration has at least 1 child it means that
            // we will use composite converter
            if ( childConfiguration.getChildCount() > 0 )
            {
                CompositeConverter converter = converterLookup.lookupCompositeConverterForType( fieldType );

                if ( converter == null )
                {
                    String msg = "Error occured while configuring component ["
                            + componentDescriptor.getHumanReadableKey()
                            + "] No converter is capable to convert configuration entry <"
                            + elementName
                            + ">"
                            + " to instance of class: '"
                            + field.getType()
                            + "' Field name: '"
                            + fieldName
                            + "', declaring class: ' "
                            + object.getClass().getName()
                            + "'";

                    throw new ComponentConfigurationException( msg );
                }

                Object value = converter.fromConfiguration( converterLookup, childConfiguration, fieldType, classLoader, componentDescriptor );

                setFieldValue( field, object, value, componentDescriptor );
            }
            else
            {
                String configValue = null;

                try
                {
                    configValue = childConfiguration.getValue();
                }
                catch ( PlexusConfigurationException e )
                {
                    String msg = "Error occured while reading config elment '"
                            + elementName
                            + "' of component "
                            + componentDescriptor.getHumanReadableKey();

                    throw new ComponentConfigurationException( msg, e );
                }

                Converter converter = ( Converter ) converterLookup.lookupBasicConverterForType( fieldType );

                if ( converter != null )
                {
                    Object value = converter.fromString( configValue );

                    setFieldValue( field, object, value, componentDescriptor );
                }
                else
                {
                    // we do not have an appropriate converter
                    // one thing which we can possibly do is to check
                    // if class have a constructor which takes String
                    // as the only parameter
                    // With that trick we won't need converters for classes
                    // like BigDecimal, BigInteger etc
                    //
                    // but I am not sure if this is a good idea in genral

                    String msg = "Error occured while configuring component ["
                            + componentDescriptor.getHumanReadableKey()
                            + "] No converter is capable to convert configuration entry <"
                            + elementName
                            + ">"
                            + configValue +
                            "</" + elementName
                            + "> to instance of class: '"
                            + field.getType()
                            + "' Field name: '"
                            + fieldName
                            + "', declaring class: ' "
                            + object.getClass().getName()
                            + "'";

                    throw new ComponentConfigurationException( msg );
                }
            }
        }
    }


    private void setFieldValue( Field field, Object object, Object value, ComponentDescriptor componentDescriptor )
            throws ComponentConfigurationException
    {
        try
        {
            boolean wasAccessible = field.isAccessible();

            if ( !wasAccessible )
            {
                field.setAccessible( true );
            }

            field.set( object, value );

            if ( !wasAccessible )
            {
                field.setAccessible( false );
            }
        }
        catch ( IllegalAccessException e )
        {
            String msg = "Error configuring component: "
                    + componentDescriptor.getHumanReadableKey()
                    + ". Cannot access field: '"
                    + field.getName() +
                    " in class: '"
                    + object.getClass().getName()
                    + "'";

            throw new ComponentConfigurationException( msg );
        }
    }

    private Field getFieldByName( String fieldName, Object object, ComponentDescriptor componentDescriptor )
            throws ComponentConfigurationException
    {

        Field retValue = ReflectionUtils.getFieldByNameIncludingSuperclasses( fieldName, object.getClass() );

        if ( retValue == null )
        {
            String msg = "Error configuring component: "
                    + componentDescriptor.getHumanReadableKey()
                    + ". Class '"
                    + object.getClass().getName()
                    + "' does not contain a field named '"
                    + fieldName + "'";

            throw new ComponentConfigurationException( msg );
        }

        return retValue;
    }

}
