package org.codehaus.plexus.test.list;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Valve
{
    static String ROLE = Valve.class.getName();

    void execute();

    boolean getState();
}
