package org.codehaus.plexus.configuration.xml.xstream.alias;

import com.thoughtworks.xstream.alias.NameMapper;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class HyphenatedNameMapper
    implements NameMapper
{
    // first-name --> firstName
    public String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    public String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }
}
