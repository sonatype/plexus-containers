package org.codehaus.plexus.test.map;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface ActivityManager
{
    static String ROLE = ActivityManager.class.getName();

    void execute( String id );

    Activity getActivity( String id );
    
    int getActivityCount();
}
