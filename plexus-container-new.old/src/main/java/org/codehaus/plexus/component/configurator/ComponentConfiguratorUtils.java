package org.codehaus.plexus.component.configurator;

import org.codehaus.plexus.util.StringUtils;

public class ComponentConfiguratorUtils
{
     // first-name --> firstName
    public static String fromXML( String elementName )
    {
        return StringUtils.lowercaseFirstLetter( StringUtils.removeAndHump( elementName, "-" ) );
    }

    // firstName --> first-name
    public static String toXML( String fieldName )
    {
        return StringUtils.addAndDeHump( fieldName );
    }
}
