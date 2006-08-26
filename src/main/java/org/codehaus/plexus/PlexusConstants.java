package org.codehaus.plexus;

public abstract class PlexusConstants
{
    /** Context key for the variable that determines whether to load the container configuration file. */
    public static final String IGNORE_CONTAINER_CONFIGURATION = "plexus.ignoreContainerConfiguration";

    /** Location of plexus bootstrap configuration file. */
    public static final String BOOTSTRAP_CONFIGURATION = "org/codehaus/plexus/plexus-bootstrap.xml";

    /** Key used to retrieve the plexus container from the context. */
    public static final String PLEXUS_KEY = "plexus";

    /** Key used to retrieve the core classworlds realm from the context.*/
    public static final String PLEXUS_CORE_REALM = "containerRealm";

}
