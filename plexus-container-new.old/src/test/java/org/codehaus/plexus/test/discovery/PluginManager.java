package org.codehaus.plexus.test.discovery;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface PluginManager
{
    String ROLE = PluginManager.class.getName();

    boolean getDiscoveryEventRegistered();
}
