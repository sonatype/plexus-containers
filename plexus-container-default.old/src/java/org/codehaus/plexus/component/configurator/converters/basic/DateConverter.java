package org.codehaus.plexus.component.configurator.converters.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter extends AbstractBasicConverter
{
    /***
     * @todo DateFormat is not thread safe!
     */
    private static final DateFormat[] formats = {
        new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S a" ),
        new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssa" )
    };

    public boolean canConvert( Class type )
    {
        return type.equals( Date.class );
    }

    public Object fromString( String str )
    {
        for ( int i = 0; i < formats.length; i++ )
        {
            try
            {
                return formats[i].parse( str );
            }
            catch ( ParseException e )
            {
                // no worries, let's try the next format.
            }
        }

        return null;
    }

    public String toString( Object obj )
    {
        Date date = (Date) obj;
        return formats[0].format( date );
    }

}
