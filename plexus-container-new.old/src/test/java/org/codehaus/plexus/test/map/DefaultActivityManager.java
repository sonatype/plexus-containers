package org.codehaus.plexus.test.map;

import java.util.Map;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultActivityManager
    implements ActivityManager
{
    private Map activities;

    public void execute( String id )
    {
        getActivity( id ).execute();
    }

    public Activity getActivity( String id )
    {
        return (Activity) activities.get( id );
    }
}
