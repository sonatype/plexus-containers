package org.codehaus.plexus.logging;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractLoggerManager
    implements LoggerManager
{
    /** */
    public AbstractLoggerManager()
    {        
    }

    public void setThreshold( String role, int threshold )
    {
        setThreshold( role, null, threshold );
    }

    public int getThreshold( String role )
    {
        return getThreshold( role, null );
    }

    public Logger getLoggerForComponent( String role )
    {
        return getLoggerForComponent( role, null );
    }

    public void returnComponentLogger( String role )
    {
        returnComponentLogger( role, null );
    }

    /**
     * Creates a string key useful as keys in <code>Map</code>'s.
     * 
     * @param role The component role.
     * @param roleHint The component role hint.
     * @return Returns a string thats useful as a key for components.
     */
    protected String toMapKey( String role, String roleHint )
    {
         if ( roleHint == null )
         {
             return role;
         }
         else
         {
             return role + ":" + roleHint;
         }
    }
}
