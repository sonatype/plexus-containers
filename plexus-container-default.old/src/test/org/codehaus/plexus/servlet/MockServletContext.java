package org.codehaus.plexus.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class MockServletContext implements ServletContext
{

    public MockServletContext() {
        
    }
    
    public ServletContext getContext(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    public int getMajorVersion()
    {
        throw new RuntimeException("not implemented");
    }

    public int getMinorVersion()
    {
        throw new RuntimeException("not implemented");
    }

    public String getMimeType(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    public Set getResourcePaths(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    public URL getResource(String arg0) throws MalformedURLException
    {
        throw new RuntimeException("not implemented");
    }

    public InputStream getResourceAsStream(String resourceName)
    {
        if (resourceName.equals("/WEB-INF/plexus.xml"))
        {
            return MockServletContext.class.getResourceAsStream("plexus.xml");
        }
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
     */
    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    /**
     * @deprecated
     */
    public Servlet getServlet(String arg0) throws ServletException
    {
        throw new RuntimeException("not implemented");
    }

    /**
     * @deprecated
     */
    public Enumeration getServlets()
    {
        throw new RuntimeException("not implemented");
    }

    /**
     * @deprecated
     */
    public Enumeration getServletNames()
    {
        throw new RuntimeException("not implemented");
    }

    public void log(String arg0)
    {
        System.out.println(arg0);
    }

    /**
     * @deprecated
     */
    public void log(Exception arg0, String arg1)
    {
        System.out.println(arg1);
        arg0.printStackTrace();
    }

    public void log(String arg0, Throwable arg1)
    {
        System.out.println(arg0);
        arg1.printStackTrace();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
     */
    public String getRealPath(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    public String getServerInfo()
    {
        throw new RuntimeException("not implemented");
    }

    public String getInitParameter(String arg0)
    {
        throw new RuntimeException("not implemented");
    }

    public Enumeration getInitParameterNames()
    {
        throw new RuntimeException("not implemented");
    }

    Map attributes = new HashMap();
    public Object getAttribute(String arg0)
    {
        return attributes.get(arg0);
    }

    public Enumeration getAttributeNames()
    {
        Vector v = new Vector(attributes.keySet());
        return v.elements();
    }

    public void setAttribute(String arg0, Object arg1)
    {
        attributes.put(arg0, arg1);
    }

    public void removeAttribute(String arg0)
    {
        attributes.remove(arg0);
    }

    public String getServletContextName()
    {
        throw new RuntimeException("not implemented");
    }

}
