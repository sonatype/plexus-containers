package org.codehaus.plexus.test;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Component
{
    static String ROLE = Component.class.getName();

    String getHost();

    int getPort();
}
