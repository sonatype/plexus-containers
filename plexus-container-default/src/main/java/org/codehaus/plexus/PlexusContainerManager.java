/*
 * $Id$
 */

package org.codehaus.plexus;

/**
 * <code>PlexusContainerManager</code> defines the interface for Plexus
 * components that can create and manage Plexus containers. An
 * implementation of this interface will configure and create Plexus
 * containers according to some policy that the component defines;
 * for example, a container factory might create a Plexus container for
 * each JAR file that exists in a given directory.
 *
 * @author <a href="mailto:mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public interface PlexusContainerManager
{
    String ROLE = PlexusContainerManager.class.getName();

    PlexusContainer[] getManagedContainers();
}
