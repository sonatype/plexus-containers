package org.codehaus.plexus.component.repository.io;

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.Reader;
import java.io.StringReader;


/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo these are all really tools for dealing with xml configurations so they
 * should be packaged as such.
 */
public class PlexusTools
{
    public static PlexusConfiguration buildConfiguration( Reader configuration )
        throws Exception
    {
        return new XmlPlexusConfiguration( Xpp3DomBuilder.build( configuration ) );
    }

    public static PlexusConfiguration buildConfiguration( String configuration )
        throws Exception
    {
        return buildConfiguration( new StringReader( configuration ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( String s )
        throws Exception
    {
        return buildComponentDescriptor( buildConfiguration( s ) );
    }

    public static ComponentDescriptor buildComponentDescriptor( PlexusConfiguration c )
        throws Exception
    {
        ComponentDescriptor cd = new ComponentDescriptor();

        cd.setRole( c.getChild( "role" ).getValue() );

        cd.setRoleHint( c.getChild( "role-hint" ).getValue() );

        cd.setImplementation( c.getChild( "implementation" ).getValue() );

        cd.setVersion( c.getChild( "version" ).getValue() );

        cd.setComponentType( c.getChild( "component-type" ).getValue() );

        cd.setInstantiationStrategy( c.getChild( "instantiation-strategy" ).getValue() );

        cd.setLifecycleHandler( c.getChild( "lifecycle-handler" ).getValue() );

        cd.setComponentProfile( c.getChild( "component-profile" ).getValue() );

        cd.setComponentComposer( c.getChild( "component-composer" ).getValue() );

        cd.setComponentFactory( c.getChild( "component-factory" ).getValue() );

        cd.setDescription( c.getChild( "description" ).getValue() );

        cd.setAlias( c.getChild( "alias" ).getValue() );

        String s = c.getChild( "isolated-realm" ).getValue();

        if ( s != null )
        {
            cd.setIsolatedRealm( s.equals( "true" ) ? true : false );
        }

        cd.setConfiguration( c.getChild( "configuration" ) );

        // ----------------------------------------------------------------------
        // Requirements
        // ----------------------------------------------------------------------

        PlexusConfiguration[] requirements = c.getChild( "requirements" ).getChildren( "requirement" );

        for ( int i = 0; i < requirements.length; i++ )
        {
            PlexusConfiguration requirement = requirements[i];

            ComponentRequirement cr = new ComponentRequirement();

            cr.setRole( requirement.getChild( "role" ).getValue() );

            cr.setRoleHint( requirement.getChild( "role-hint" ).getValue() );

            cr.setFieldName( requirement.getChild( "field-name" ).getValue() );

            cd.addRequirement( cr );
        }

        return cd;
    }

    public static ComponentSetDescriptor buildComponentSet( PlexusConfiguration c )
        throws Exception
    {
        ComponentSetDescriptor csd = new ComponentSetDescriptor();

        // ----------------------------------------------------------------------
        // Components
        // ----------------------------------------------------------------------

        PlexusConfiguration[] components = c.getChild( "components" ).getChildren( "component" );

        for ( int i = 0; i < components.length; i++ )
        {
            PlexusConfiguration component = components[i];

            csd.addComponentDescriptor( buildComponentDescriptor( component ) );
        }

        // ----------------------------------------------------------------------
        // Dependencies
        // ----------------------------------------------------------------------

        PlexusConfiguration[] dependencies = c.getChild( "dependencies" ).getChildren( "dependency" );

        for ( int i = 0; i < dependencies.length; i++ )
        {
            PlexusConfiguration d = dependencies[i];

            ComponentDependency cd = new ComponentDependency();

            cd.setArtifactId( d.getChild( "artifact-id" ).getValue() );

            cd.setGroupId( d.getChild( "group-id" ).getValue() );

            cd.setType( d.getChild( "type" ).getValue() );

            cd.setVersion( d.getChild( "version" ).getValue() );

            csd.addDependency( cd );
        }

        return csd;
    }
}
