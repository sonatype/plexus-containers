package org.codehaus.plexus.configuration.xml;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;

// NOTE: This is meant to help us avoid storing Xpp3Dom instances by serializing them to string
// and only deserializing when necessary. This should help in the container, since every
// ComponentDescriptor contains one of these...they are very prolific.
public class ProtoXmlPlexusConfiguration
    implements PlexusConfiguration
{

    private String xml;

    private WeakReference configHolder;

    public ProtoXmlPlexusConfiguration( XmlPlexusConfiguration config )
    {
        store( config );
    }

    private synchronized void store( XmlPlexusConfiguration config )
    {
        xml = config.getXpp3Dom().toString();
        configHolder = new WeakReference( config );
    }

    public XmlPlexusConfiguration read()
    {
        XmlPlexusConfiguration config = (XmlPlexusConfiguration) ( configHolder == null ? null : configHolder.get() );

        if ( config == null )
        {
            try
            {
                config = new XmlPlexusConfiguration( Xpp3DomBuilder.build( new StringReader( xml ) ) );
            }
            catch ( XmlPullParserException e )
            {
                IllegalStateException error = new IllegalStateException( "Failed to re-read stored XML plexus configuration." );
                error.initCause( e );

                throw error;
            }
            catch ( IOException e )
            {
                IllegalStateException error = new IllegalStateException( "Failed to re-read stored XML plexus configuration." );
                error.initCause( e );

                throw error;
            }
        }

        return config;
    }

    public void addChild( PlexusConfiguration configuration )
    {
        XmlPlexusConfiguration config = read();
        config.addChild( configuration );
        store( config );
    }

    public PlexusConfiguration addChild( String name )
    {
        XmlPlexusConfiguration config = read();
        PlexusConfiguration configuration = config.addChild( name );
        store( config );
        return configuration;
    }

    public PlexusConfiguration addChild( String name, String value )
    {
        XmlPlexusConfiguration config = read();
        PlexusConfiguration configuration = config.addChild( name ).setValue( value );
        store( config );
        return configuration;
    }

    public String getAttribute( String paramName )
        throws PlexusConfigurationException
    {
        return read().getAttribute( paramName );
    }

    public String getAttribute( String name,
                                String defaultValue )
    {
        return read().getAttribute( name, defaultValue );
    }

    public String[] getAttributeNames()
    {
        return read().getAttributeNames();
    }

    public PlexusConfiguration getChild( String child )
    {
        return read().getChild( child );
    }

    public PlexusConfiguration getChild( int i )
    {
        return read().getChild( i );
    }

    public PlexusConfiguration getChild( String child,
                                         boolean createChild )
    {
        return read().getChild( child, createChild );
    }

    public int getChildCount()
    {
        return read().getChildCount();
    }

    public PlexusConfiguration[] getChildren()
    {
        return read().getChildren();
    }

    public PlexusConfiguration[] getChildren( String name )
    {
        return read().getChildren( name );
    }

    public String getName()
    {
        return read().getName();
    }

    public String getValue()
        throws PlexusConfigurationException
    {
        return read().getValue();
    }

    public PlexusConfiguration setValue( String value )
    {
        read().setValue( value );

        return this;
    }

    public String getValue( String defaultValue )
    {
        return read().getValue( defaultValue );
    }

}
