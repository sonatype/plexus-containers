package org.codehaus.plexus.component.configurator.converters;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractConfigurationConverter implements ConfigurationConverter
{
    private static final String IMPLEMENTATION = "implementation";

    /**
     * We will check if user has provided a hint which class should be used for given field.
     * So we will check if something like <foo implementation="com.MyFoo"> is present in configuraion.
     * If 'implementation' hint was provided we will try to load correspoding class
     * If we are unable to do so error will be reported
     */
    protected Class getClassForImplementationHint( Class type,
                                                   PlexusConfiguration configuration,
                                                   ClassLoader classLoader,
                                                   ComponentDescriptor componentDescriptor )
            throws ComponentConfigurationException
    {
        Class retValue = type;

        String implementation = configuration.getAttribute( IMPLEMENTATION, null );

        if ( implementation != null )
        {
            try
            {
                retValue = classLoader.loadClass( implementation );

            }
            catch ( ClassNotFoundException e )
            {
                String msg = "Error configuring component: "
                        + componentDescriptor.getHumanReadableKey()
                        + ". Class name which was explicitly "
                        + " given in configuration using 'implementation' attribute: '"
                        + implementation + "' cannot be loaded: " + e.getMessage();

                throw new ComponentConfigurationException( msg );
            }
        }


        return retValue;
    }


    protected Class loadClass( String classname, ClassLoader classLoader, ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Class retValue = null;

        try
        {
            retValue = classLoader.loadClass( classname );
        }
        catch ( Exception e )
        {
            throw new ComponentConfigurationException( "Error configuring component: " + componentDescriptor.getHumanReadableKey() + ".", e );
        }

        return retValue;
    }

    protected Object instantiateObject( String classname, ClassLoader classLoader, ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Class clazz = loadClass( classname, classLoader, componentDescriptor );

        Object retValue = instantiateObject( clazz, componentDescriptor );

        return retValue;
    }

    protected Object instantiateObject( Class clazz, ComponentDescriptor componentDescriptor ) throws ComponentConfigurationException
    {
        Object retValue = null;

        try
        {
            retValue = clazz.newInstance();

            return retValue;
        }
        catch ( Exception e )
        {
            String msg = "Error configuring component: "
                    + componentDescriptor.getHumanReadableKey()
                    + ". Class '"
                    + clazz.getName()
                    + "' cannot be instantiated";


            throw new ComponentConfigurationException( msg );
        }
    }


    // first-name --> firstName
    protected String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    protected  String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }

}
