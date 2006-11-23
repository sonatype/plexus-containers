package org.codehaus.plexus.component.configurator.converters.composite;

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
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.ConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author <a href="mailto:kenney@codehaus.org">Kenney Westerhof</a>
 * @version $Id$
 */
public class ArrayConverter
    extends AbstractConfigurationConverter
{
    public boolean canConvert( Class type )
    {
        return type.isArray();
    }

    public Object fromConfiguration( ConverterLookup converterLookup, PlexusConfiguration configuration, Class type,
                                     Class baseType, ClassRealm classRealm, ExpressionEvaluator expressionEvaluator,
                                     ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        Object retValue = fromExpression( configuration, expressionEvaluator, type );
        if ( retValue != null )
        {
            return retValue;
        }

        List values = new ArrayList();

        for ( int i = 0; i < configuration.getChildCount(); i++ )
        {
            PlexusConfiguration c = configuration.getChild( i );

            String configEntry = c.getName();

            String name = fromXML( configEntry );

            Class childType = getClassForImplementationHint( null, c, classRealm );

            // check if the name is a fully qualified classname

            if ( childType == null && name.indexOf( '.' ) > 0 )
            {
                try
                {
                    childType = classRealm.loadClass( name );
                }
                catch ( ClassNotFoundException e )
                {
                    // doesn't exist - continue processing
                }
            }

            if ( childType == null )
            {
                // try to find the class in the package of the baseType
                // (which is the component being configured)

                String baseTypeName = baseType.getName();

                int lastDot = baseTypeName.lastIndexOf( '.' );

                String className;

                if ( lastDot == -1 )
                {
                    className = name;
                }
                else
                {
                    String basePackage = baseTypeName.substring( 0, lastDot );

                    className = basePackage + "." + StringUtils.capitalizeFirstLetter( name );
                }

                try
                {
                    childType = classRealm.loadClass( className );
                }
                catch ( ClassNotFoundException e )
                {
                    // doesn't exist, continue processing
                }
            }

            // finally just try the component type of the array

            if ( childType == null )
            {
                childType = type.getComponentType();
            }

            ConfigurationConverter converter = converterLookup.lookupConverterForType( childType );

            Object object = converter.fromConfiguration( converterLookup, c, childType, baseType, classRealm,
                                                         expressionEvaluator, listener );

            values.add( object );
        }
        
        Class componentType = type.getComponentType();

        if ( componentType.isPrimitive() && !values.isEmpty() )
        {
            Iterator it = values.iterator();
            while ( it.hasNext() )
            {
                it.next().getClass().getName();
                throw new ComponentConfigurationException( "Can't convert " + it.next().getClass().getName()
                    + " List to " + componentType.getName() + " array" );
            }
        }

        return values.toArray( (Object[]) Array.newInstance( componentType, 0 ) );
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
