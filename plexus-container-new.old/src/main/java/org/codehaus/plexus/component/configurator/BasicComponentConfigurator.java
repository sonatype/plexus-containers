package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.component.configurator.converters.basic.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class BasicComponentConfigurator
    implements ComponentConfigurator
{
    private Map converterMap;

    public BasicComponentConfigurator()
    {
        converterMap = new HashMap();

        converterMap.put( boolean.class, new BooleanConverter() );
        converterMap.put( byte.class, new ByteConverter() );
        converterMap.put( char.class, new CharConverter() );
        converterMap.put( double.class, new DoubleConverter() );
        converterMap.put( float.class, new FloatConverter() );
        converterMap.put( int.class, new IntConverter() );
        converterMap.put( long.class, new LongConverter() );
        converterMap.put( short.class, new ShortConverter() );

        converterMap.put( StringBuffer.class, new StringBufferConverter() );
        converterMap.put( String.class, new StringConverter() );
        converterMap.put( Date.class, new DateConverter() );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        // ----------------------------------------------------------------------
        // We should probably take into consideration the realm that the component
        // came from in order to load the correct classes.
        // ----------------------------------------------------------------------

        processConfiguration( component, configuration );
    }

    protected void processConfiguration( Object component, PlexusConfiguration configuration )
        throws ComponentConfigurationException
    {
        int items = configuration.getChildCount();

        for ( int i = 0; i < items; i++ )
        {
            PlexusConfiguration c = configuration.getChild( i );

            if ( c.getChildCount() > 0 )
            {
                // We have a collection or an object

                Field field = getField( configuration, component );

                // o Get the field type
                //

                processConfiguration( component, c );
            }
            else
            {
                String fieldName = fromXML( c.getName() );

                try
                {
                    Field field = component.getClass().getDeclaredField( fieldName );

                    field.setAccessible( true );

                    Converter converter = (Converter) converterMap.get( field.getType() );

                    if ( converter != null )
                    {
                        field.set( component, converter.fromString( c.getValue() ) );
                    }
                    else
                    {
                        // We have a field that is not primitive so we must instantiate
                        // the necessary objects and populate them.
                    }
                }
                catch ( NoSuchFieldException e )
                {
                    throw new ComponentConfigurationException(
                        "The class " + component.getClass() + " does not contain a field named " + fieldName );
                }
                catch ( PlexusConfigurationException e )
                {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected Field getField( PlexusConfiguration c, Object o )
        throws ComponentConfigurationException
    {
        String fieldName = fromXML( c.getName() );

        try
        {
            return o.getClass().getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e )
        {
            throw new ComponentConfigurationException(
                "The class " + o.getClass() + " does not contain a field named " + fieldName );
        }
    }

    // ----------------------------------------------------------------------
    // Conversions
    // ----------------------------------------------------------------------

    // first-name --> firstName
    public String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    public String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }
}
