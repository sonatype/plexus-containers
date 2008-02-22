package org.codehaus.plexus.aspect;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.discovery.ComponentDiscoveryListener;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.DuplicateChildContainerException;
import org.codehaus.plexus.PlexusContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public aspect CompatibilityAspect
{

    public PlexusContainer PlexusContainer.createChildContainer( String name,
                                                 List classpath,
                                                 Map context )
        throws PlexusContainerException
    {
        return createChildContainer( name, classpath, context, Collections.EMPTY_LIST );
    }

    public PlexusContainer PlexusContainer.createChildContainer( String name,
                                                 List classpath,
                                                 Map context,
                                                 List discoveryListeners )
        throws PlexusContainerException
    {
        if ( hasChildContainer( name ) )
        {
            throw new DuplicateChildContainerException( getName(), name );
        }

        ClassRealm realm = new ClassRealm( new ClassWorld(), name );
        for ( Iterator it = classpath.iterator(); it.hasNext(); )
        {
            File jar = (File) it.next();
            try
            {
                realm.addURL( jar.toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (bad URL)", e );
            }
        }

        ContainerConfiguration config = new DefaultContainerConfiguration();

        config.setRealm( realm );
        config.setName( name );
        config.setParentContainer( this );
        config.setContext( context );

        for ( Iterator it = discoveryListeners.iterator(); it.hasNext(); )
        {
            ComponentDiscoveryListener listener = (ComponentDiscoveryListener) it.next();
            config.addComponentDiscoveryListener( listener );
        }

        return new DefaultPlexusContainer( config );
    }

}
