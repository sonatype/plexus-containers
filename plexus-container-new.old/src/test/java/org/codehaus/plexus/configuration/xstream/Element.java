package org.codehaus.plexus.configuration.xstream;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Element
{
    private String headerKey;

    private String expression;

    public String getHeaderKey()
    {
        return headerKey;
    }

    public void setHeaderKey( String headerKey )
    {
        this.headerKey = headerKey;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
}
