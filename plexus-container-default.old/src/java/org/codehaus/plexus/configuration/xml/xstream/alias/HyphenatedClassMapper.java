package org.codehaus.plexus.configuration.xml.xstream.alias;

import com.thoughtworks.xstream.alias.DefaultClassMapper;
import com.thoughtworks.xstream.alias.CannotResolveClassException;
import com.thoughtworks.xstream.alias.NameMapper;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class HyphenatedClassMapper
    extends DefaultClassMapper
{
    private String basePackage;

    public HyphenatedClassMapper( NameMapper elementMapper )
    {
        super( elementMapper );
    }

    public void alias( String elementName, Class type, Class defaultImplementation )
    {
        if ( elementName.equals( "basePackage" ) )
        {
            basePackage = type.getName();

            if (  basePackage.lastIndexOf( "." ) != -1 )
            {
            	basePackage = basePackage.substring( 0, basePackage.lastIndexOf( "." ) );
            }
            else
            {
                basePackage = "";   
            }
            
            return;
        }

        super.alias( elementName, type, defaultImplementation );
    }

    public Class lookupType( String elementName )
    {        
        if ( elementName.equals( "null" ) )
        {
            return null;
        }

        boolean isArray = elementName.endsWith( "-array" );

        if ( isArray )
        {
            elementName = elementName.substring( 0, elementName.length() - 6 ); // cut off -array
        }

        String mappedName = (String) nameToTypeMap.get( elementName );

        if ( mappedName != null )
        {
            elementName = mappedName;
        }
        else if ( elementName.indexOf( "." ) < 0 )
        {
            elementName = StringUtils.capitalizeFirstLetter( mapNameFromXML( elementName ) );

            elementName = basePackage + "." + elementName;
        }

        // the $ used in inner class names is illegal as an xml element name
        elementName = elementName.replaceAll( "\\-", "\\$" );

        try
        {
            if ( isArray )
            {
                return Thread.currentThread().getContextClassLoader().loadClass( "[L" + elementName + ";" );
            }
            else
            {
                return Thread.currentThread().getContextClassLoader().loadClass( elementName );
            }
        }
        catch ( ClassNotFoundException e )
        {
            throw new CannotResolveClassException( elementName );
        }
    }
}