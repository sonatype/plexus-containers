package org.codehaus.plexus.component;

/**
 * @plexus.component 
 */
public class HelloImpl
    implements Hello
{

    /**
     * @plexus.configuration expression="${expression}" default-value="Hello World!"
     * @required
     */
    private String message;

    public void sayHello()
    {
        System.out.print( message );
    }

}