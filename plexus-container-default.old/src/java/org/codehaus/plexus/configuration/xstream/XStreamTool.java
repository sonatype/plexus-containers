package org.codehaus.plexus.configuration.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.alias.CannotResolveClassException;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.alias.ElementMapper;
import com.thoughtworks.xstream.objecttree.reflection.JavaReflectionObjectFactory;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.builder.XmlPullConfigurationBuilder;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class XStreamTool
{
    private XStream xstream;

    public XStreamTool()
    {
        HyphenatedElementMapper elementMapper = new HyphenatedElementMapper();

        HyphenatedClassMapper classMapper = new HyphenatedClassMapper( elementMapper );

        xstream = new XStream( new JavaReflectionObjectFactory(),
                               classMapper,
                               elementMapper );

        xstream.registerConverter( new CollectionConverter( classMapper ) );

        xstream.registerConverter( new ConfigurationConverter() );
    }

    public void alias( String elementName, Class clazz )
    {
        xstream.alias( elementName, clazz );
    }

    public Object build( Reader reader, Class clazz )
        throws Exception
    {                 x
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();

        return build( builder.parse( reader ), clazz );
    }

    public Object build( PlexusConfiguration configuration, Class clazz )
        throws Exception
    {
        ConfigurationReader reader = new ConfigurationReader( configuration );

        xstream.alias( "basePackage", clazz );

        Object object = xstream.fromXML( reader );

        return object;
    }

    public DefaultConfiguration write( Object o )
        throws Exception
    {
        ConfigurationWriter writer = new ConfigurationWriter();

        xstream.toXML( o, writer );

        return writer.getConfiguration();
    }

    public String addAndDeHump( String view )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < view.length(); i++ )
        {
            if ( i != 0 && Character.isUpperCase( view.charAt( i ) ) )
            {
                sb.append( '-' );
            }

            sb.append( view.charAt( i ) );
        }

        return sb.toString().trim().toLowerCase();
    }

    public String removeAndHump( String data, String replaceThis )
    {
        String temp = null;
        StringBuffer out = new StringBuffer();
        temp = data;

        StringTokenizer st = new StringTokenizer( temp, replaceThis );

        while ( st.hasMoreTokens() )
        {
            String element = (String) st.nextElement();
            out.append( capitalizeFirstLetter( element ) );
        }

        return out.toString();
    }

    public String capitalizeFirstLetter( String data )
    {
        String firstLetter = data.substring( 0, 1 ).toUpperCase();
        String restLetters = data.substring( 1 );
        return firstLetter + restLetters;
    }

    public String lowercaseFirstLetter( String data )
    {
        String firstLetter = data.substring( 0, 1 ).toLowerCase();
        String restLetters = data.substring( 1 );
        return firstLetter + restLetters;
    }

    private class HyphenatedElementMapper
        implements ElementMapper
    {
        public String fromXml( String elementName )
        {
            return lowercaseFirstLetter( removeAndHump( elementName, "-" ) );
        }

        public String toXml( String fieldName )
        {
            return addAndDeHump( fieldName );
        }
    }

    private class HyphenatedClassMapper
        implements ClassMapper
    {
        private Map typeToNameMap = new HashMap();
        private Map nameToTypeMap = new HashMap();
        private Map baseTypeToDefaultTypeMap = new HashMap();
        private ElementMapper elementMapper;
        private String basePackage;

        public HyphenatedClassMapper( ElementMapper elementMapper )
        {
            this.elementMapper = elementMapper;

            // register primitive types
            baseTypeToDefaultTypeMap.put( boolean.class, Boolean.class );
            baseTypeToDefaultTypeMap.put( char.class, Character.class );
            baseTypeToDefaultTypeMap.put( int.class, Integer.class );
            baseTypeToDefaultTypeMap.put( float.class, Float.class );
            baseTypeToDefaultTypeMap.put( double.class, Double.class );
            baseTypeToDefaultTypeMap.put( short.class, Short.class );
            baseTypeToDefaultTypeMap.put( byte.class, Byte.class );
            baseTypeToDefaultTypeMap.put( long.class, Long.class );
        }

        public void alias( String elementName, Class type, Class defaultImplementation )
        {
            if ( elementName.equals( "basePackage" ) )
            {
                basePackage = type.getName();

                basePackage = basePackage.substring( 0, basePackage.lastIndexOf( "." ) );

                return;
            }

            nameToTypeMap.put( elementName, type.getName() );

            typeToNameMap.put( type, elementName );

            if ( !type.equals( defaultImplementation ) )
            {
                typeToNameMap.put( defaultImplementation, elementName );
            }

            baseTypeToDefaultTypeMap.put( type, defaultImplementation );
        }

        public String lookupName( Class type )
        {
            boolean isArray = type.isArray();

            if ( type.isArray() )
            {
                type = type.getComponentType();
            }

            String result = (String) typeToNameMap.get( type );

            if ( result == null )
            {
                // the $ used in inner class names is illegal as an xml element name
                result = type.getName().replaceAll( "\\$", "-" );
            }

            if ( isArray )
            {
                result += "-array";
            }

            return result;
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
                elementName = capitalizeFirstLetter( elementMapper.fromXml( elementName ) );

                elementName = basePackage + "." + elementName;
            }

            // the $ used in inner class names is illegal as an xml element name
            elementName = elementName.replaceAll( "\\-", "\\$" );

            try
            {
                if ( isArray )
                {
                    return Class.forName( "[L" + elementName + ";" );
                }
                else
                {
                    return Class.forName( elementName );
                }
            }
            catch ( ClassNotFoundException e )
            {
                throw new CannotResolveClassException( elementName );
            }
        }

        public Class lookupDefaultType( Class baseType )
        {
            return (Class) baseTypeToDefaultTypeMap.get( baseType );
        }
    }
}
