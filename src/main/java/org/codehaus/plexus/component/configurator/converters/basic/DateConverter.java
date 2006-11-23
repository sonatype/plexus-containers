package org.codehaus.plexus.component.configurator.converters.basic;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
