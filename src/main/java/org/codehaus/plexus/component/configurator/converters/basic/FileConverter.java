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

import org.codehaus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 */
public class FileConverter
    extends AbstractBasicConverter
{
    public boolean canConvert( Class type )
    {
        return type.equals( File.class );
    }

    public Object fromString( String str )
    {
        return new File( str );
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        File f = (File) super.fromConfiguration( converterLookup, configuration, type, baseType, classRealm,
                                                 expressionEvaluator, listener );

        if ( f != null )
        {
            // Hmmm... is this cheating? Can't think of a better way right now
            return expressionEvaluator.alignToBaseDirectory( f );
        }
        else
        {
            return null;
        }
    }
}
