package org.codehaus.plexus.component.repository.io;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.configuration.xml.Xpp3Dom;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentDescriptorReader
{
    public ComponentDescriptor parseComponentDescriptor( Reader reader )
        throws Exception
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser parser = factory.newPullParser();

        parser.setInput( reader );

        ComponentDescriptor cd = new ComponentDescriptor();

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "component" ) )
                {
                    cd = parseComponentDescriptorBody( parser );
                }
            }

            eventType = parser.next();
        }

        return cd;
    }

    public ComponentSetDescriptor parseComponentSetDescriptor( Reader reader )
        throws Exception
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        XmlPullParser parser = factory.newPullParser();

        parser.setInput( reader );

        return parseComponentSetDescriptorBody( parser );
    }

    public ComponentSetDescriptor parseComponentSetDescriptorBody( XmlPullParser parser )
        throws Exception
    {
        ComponentSetDescriptor csd = new ComponentSetDescriptor();

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "component-set" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "components" ) )
                        {
                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "component" ) )
                                {
                                    ComponentDescriptor cd = parseComponentDescriptorBody( parser );

                                    csd.addComponentDescriptor( cd );
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else if ( parser.getName().equals( "dependencies" ) )
                        {
                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "dependency" ) )
                                {
                                    ComponentDependency cr = new ComponentDependency();

                                    csd.addDependency( cr );

                                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                                    {
                                        if ( parser.getName().equals( "group-id" ) )
                                        {
                                            cr.setGroupId( parser.nextText() );
                                        }
                                        else if ( parser.getName().equals( "artifact-id" ) )
                                        {
                                            cr.setArtifactId( parser.nextText() );
                                        }
                                        else if ( parser.getName().equals( "version" ) )
                                        {
                                            cr.setVersion( parser.nextText() );
                                        }
                                        else
                                        {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else if ( parser.getName().equals( "isolated-realm" ) )
                        {
                            String s = parser.nextText();

                            boolean b = s.equals( "true" ) ? true : false;

                            csd.setIsolatedRealm( b );
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
            }

            eventType = parser.next();
        }

        return csd;
    }

    // ----------------------------------------------------------------------
    // parse component descriptor body
    // ----------------------------------------------------------------------

    protected ComponentDescriptor parseComponentDescriptorBody( XmlPullParser parser )
        throws Exception
    {
        ComponentDescriptor cd = new ComponentDescriptor();

        while ( parser.nextTag() == XmlPullParser.START_TAG )
        {
            if ( parser.getName().equals( "role" ) )
            {
                cd.setRole( parser.nextText() );
            }
            else if ( parser.getName().equals( "role-hint" ) )
            {
                cd.setRoleHint( parser.nextText() );
            }
            else if ( parser.getName().equals( "implementation" ) )
            {
                cd.setImplementation( parser.nextText() );
            }
            else if ( parser.getName().equals( "version" ) )
            {
                cd.setVersion( parser.nextText() );
            }
            else if ( parser.getName().equals( "component-type" ) )
            {
                cd.setComponentType( parser.nextText() );
            }
            else if ( parser.getName().equals( "instantiation-strategy" ) )
            {
                cd.setInstantiationStrategy( parser.nextText() );
            }
            else if ( parser.getName().equals( "lifecycle-handler" ) )
            {
                cd.setLifecycleHandler( parser.nextText() );
            }
            else if ( parser.getName().equals( "component-profile" ) )
            {
                cd.setComponentProfile( parser.nextText() );
            }
            else if ( parser.getName().equals( "component-composer" ) )
            {
                cd.setComponentComposer( parser.nextText() );
            }
            else if ( parser.getName().equals( "component-factory" ) )
            {
                cd.setComponentFactory( parser.nextText() );
            }
            else if ( parser.getName().equals( "description" ) )
            {
                cd.setDescription( parser.nextText() );
            }
            else if ( parser.getName().equals( "alias" ) )
            {
                cd.setAlias( parser.nextText() );
            }
            else if ( parser.getName().equals( "requirements" ) )
            {
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "requirement" ) )
                    {
                        ComponentRequirement cr = new ComponentRequirement();

                        cd.addRequirement( cr );

                        while ( parser.nextTag() == XmlPullParser.START_TAG )
                        {
                            if ( parser.getName().equals( "role" ) )
                            {
                                cr.setRole( parser.nextText() );
                            }
                            else if ( parser.getName().equals( "field-name" ) )
                            {
                                cr.setFieldName( parser.nextText() );
                            }
                            else
                            {
                                parser.nextText();
                            }
                        }
                    }
                    else
                    {
                        parser.nextText();
                    }
                }
            }
            else if ( parser.getName().equals( "configuration" ) )
            {
                cd.setConfiguration( parseConfiguration( parser ) );
            }
            else
            {
                parser.nextText();
            }
        }

        return cd;
    }

    // ----------------------------------------------------------------------
    // parse configuration body
    // ----------------------------------------------------------------------

    protected PlexusConfiguration parseConfiguration( XmlPullParser parser )
        throws Exception
    {
        List elements = new ArrayList();

        List values = new ArrayList();

        Xpp3Dom configuration = null;

        int eventType = parser.getEventType();

        // ----------------------------------------------------------------------
        // We just want to parse <configuration>...</configuration>
        // ----------------------------------------------------------------------

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                String rawName = parser.getName();

                Xpp3Dom childConfiguration = createConfiguration( rawName );

                int depth = elements.size();

                if ( depth > 0 )
                {
                    Xpp3Dom parent = (Xpp3Dom) elements.get( depth - 1 );

                    parent.addChild( childConfiguration );
                }

                elements.add( childConfiguration );

                values.add( new StringBuffer() );

                int attributesSize = parser.getAttributeCount();

                for ( int i = 0; i < attributesSize; i++ )
                {
                    String name = parser.getAttributeName( i );

                    String value = parser.getAttributeValue( i );

                    childConfiguration.setAttribute( name, value );
                }
            }
            else if ( eventType == XmlPullParser.TEXT )
            {
                int depth = values.size() - 1;

                StringBuffer valueBuffer = (StringBuffer) values.get( depth );

                valueBuffer.append( parser.getText() );
            }
            else if ( eventType == XmlPullParser.END_TAG )
            {
                int depth = elements.size() - 1;

                Xpp3Dom finishedConfiguration = (Xpp3Dom) elements.remove( depth );

                String accumulatedValue = ( values.remove( depth ) ).toString();

                if ( finishedConfiguration.getChildCount() == 0 )
                {
                    String finishedValue;

                    if ( 0 == accumulatedValue.length() )
                    {
                        finishedValue = null;
                    }
                    else
                    {
                        finishedValue = accumulatedValue;
                    }

                    finishedConfiguration.setValue( finishedValue );
                }

                if ( 0 == depth )
                {
                    configuration = finishedConfiguration;
                }

                if ( parser.getName().equals( "configuration" ) )
                {
                    break;
                }
            }

            eventType = parser.next();
        }

        return new XmlPlexusConfiguration( configuration );
    }

    protected Xpp3Dom createConfiguration( String localName )
    {
        return new Xpp3Dom( localName );
    }
}
