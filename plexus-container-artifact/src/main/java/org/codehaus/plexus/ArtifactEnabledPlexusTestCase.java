package org.codehaus.plexus;

/**
 * @author jdcasey
 * @version $Id$
 */
public class ArtifactEnabledPlexusTestCase
    extends PlexusTestCase
{
    public PlexusContainer getContainerInstance()
    {
        return new DefaultArtifactEnabledContainer();
    }
}
