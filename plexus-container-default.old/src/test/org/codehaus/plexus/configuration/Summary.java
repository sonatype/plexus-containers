package org.codehaus.plexus.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class Summary
{
    /** */
    private String id;

    /** */
    private String title;

    /** */
    private String keyField;

    /** */
    private String collection;

    /** Elements. */
    private List elements = new ArrayList();

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getCollection()
    {
        return collection;
    }

    public void setCollection( String collection )
    {
        this.collection = collection;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getKeyField()
    {
        return keyField;
    }

    public void setKeyField( String keyField )
    {
        this.keyField = keyField;
    }

    public List getElements()
    {
        return elements;
    }

    public void setElements( List elements )
    {
        this.elements = elements;
    }
}
